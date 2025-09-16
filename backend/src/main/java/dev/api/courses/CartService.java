package dev.api.courses;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.api.common.enums.Status;
import dev.api.common.exceptions.BusinessException;
import dev.api.common.exceptions.ResourceNotFoundException;
import dev.api.courses.model.Course;
import dev.api.courses.model.redis.CacheCart;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.model.redis.CacheStudent;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CacheCartRepository;
import dev.api.courses.repository.redis.CacheCourseRepository;
import dev.api.courses.repository.redis.CacheStudentRepository;
import dev.api.courses.requests.AddToCartRequest;
import dev.api.courses.responses.CartCoursesResponse;
import dev.api.students.repository.StudentsRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CartService {

    private CacheStudentRepository cachestudentRepository;
    private CacheCartRepository cacheCartRepository;
    private CoursesRepository coursesRepository;
    private CacheCourseRepository cacheCourseRepository;
    private StudentsRepository studentsRepository;

    public void addToCart(AddToCartRequest request, String username) {
        Course course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Check if course is available for purchase
        if (!course.getStatus().equals(Status.PUBLISHED)) {
            throw new BusinessException("Course is not available for purchase");
        }

        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        CacheCourse cacheCourse = mapToCacheCourse(course);

        // if student not in cache, create new one with new cart
        if (cacheStudent == null) {
            createNewStudentWithCart(username, cacheCourse, course.getPrice());
        } else {
            // Add to existing cart
            addToExistingCart(cacheStudent, cacheCourse, course);
        }
    }

    private void createNewStudentWithCart(String username, CacheCourse cacheCourse, BigDecimal coursePrice) {
        CacheStudent cacheStudent = new CacheStudent();
        cacheStudent.setStudentId("student_" + username);
        
        String cartId = "cart_" + UUID.randomUUID().toString();
        CacheCart cacheCart = new CacheCart();
        cacheCart.setCartId(cartId);
        cacheCart.setTotalAmount(coursePrice);
        cacheCart.getItemIds().add(cacheCourse.getId());
        
        cacheStudent.setCartId(cartId);
        
        cachestudentRepository.save(cacheStudent);
        cacheCartRepository.save(cacheCart);
        cacheCourseRepository.save(cacheCourse);

    }

    private void addToExistingCart(CacheStudent cacheStudent, CacheCourse cacheCourse, Course course) {
        Optional<CacheCart> cart = cacheCartRepository.findById(cacheStudent.getCartId());
        
        if (cart.isEmpty()) {
            // cart was deleted/expired, create new one
            String newCartId = "cart_" + UUID.randomUUID().toString();
            CacheCart newCart = new CacheCart();
            newCart.setCartId(newCartId);
            newCart.setTotalAmount(course.getPrice());
            newCart.getItemIds().add(cacheCourse.getId());
            
            cacheStudent.setCartId(newCartId);
            cachestudentRepository.save(cacheStudent);
            cacheCartRepository.save(newCart);
            cacheCourseRepository.save(cacheCourse);
            return;
        }
        
        CacheCart cacheCart = cart.get();
        
        // if course already in cart
        if (cacheCart.getItemIds().contains(cacheCourse.getId())) {
            throw new BusinessException("Course already in cart");
        }
        
        // Add course to existing cart
        cacheCart.setTotalAmount(cacheCart.getTotalAmount().add(course.getPrice()));
        cacheCart.getItemIds().add(cacheCourse.getId());
        
        cacheCartRepository.save(cacheCart);
        cacheCourseRepository.save(cacheCourse);
    }


    private CacheCourse mapToCacheCourse(Course course) {
        CacheCourse cacheCourse = new CacheCourse();
        cacheCourse.setId("course_" + course.getCourseId());
        cacheCourse.setTitle(course.getTitle());
        cacheCourse.setSubTitle(course.getSubTitle());
        cacheCourse.setDescription(course.getDescription());
        cacheCourse.setPrice(course.getPrice());
        cacheCourse.setIsFree(course.getIsFree());
        cacheCourse.setCourseDurationMinutes(course.getCourseDurationMinutes());
        cacheCourse.setLanguage(course.getLanguage());
        cacheCourse.setStatus(course.getStatus());
        cacheCourse.setLevel(course.getLevel());
        
        // Set categories - uncomment when ready
        // if (course.getMainCategories() != null) {
        //     Set<String> categories = new HashSet<>();
        //     for (MainCategories mainCategory : course.getMainCategories()) {
        //         if (mainCategory.getName() != null) {
        //             categories.add(mainCategory.getName());
        //         }
        //         if (mainCategory.getSubcategories() != null) {
        //             for (Subcategories subCategory : mainCategory.getSubcategories()) {
        //                 if (subCategory.getName() != null) {
        //                     categories.add(subCategory.getName());
        //                 }
        //             }
        //         }
        //     }
        //     cacheCourse.setCategory(categories);
        // }
        
        return cacheCourse;
    }

    public CartCoursesResponse getCart(String username) {
        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
       
        // if student not found in cache 
        if (cacheStudent == null ) {
            throw new ResourceNotFoundException("student not found"); 
        }

        // Cart expired or deleted 
        if (cacheStudent.getCartId() == null) {
            throw new BusinessException("cart expired or deleted");
        }

        CacheCart cacheCart = cacheCartRepository.findById(cacheStudent.getCartId()).get();
        Iterable<CacheCourse> cacheCourses = cacheCourseRepository.findAllById(cacheCart.getItemIds());
        
        return new CartCoursesResponse(cacheCart.getTotalAmount(), cacheCart.getItemIds().size(), cacheCourses);
    }

     public void removeItemFromCart(String courseId, String username) {
        Optional<CacheCourse> cacheCourse = cacheCourseRepository.findById(courseId);
        if (cacheCourse.isEmpty()) {
            throw new ResourceNotFoundException("Course not found in cart with id: " + courseId);
        }
        
        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        
        if (cacheStudent == null ) {
            throw new ResourceNotFoundException("student not found"); 
        }

        if (cacheStudent.getCartId() == null) {
            throw new BusinessException("cart expired or deleted");
        }

        CacheCart cacheCart = cacheCartRepository.findById(cacheStudent.getCartId()).get();
      
        // Remove item and update total
        cacheCart.getItemIds().remove(courseId);
        cacheCart.setTotalAmount(cacheCart.getTotalAmount().subtract(cacheCourse.get().getPrice()));
        
        // If cart becomes empty, delete it
        if (cacheCart.getItemIds().isEmpty()) {
            clearCart(username);
        } else {
            cacheCartRepository.save(cacheCart);
        }
        
        // Delete the course from cache
        cacheCourseRepository.delete(cacheCourse.get());
    }

    public void clearCart(String username) {
        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        if (cacheStudent != null) {
            if(cacheStudent.getCartId() == null){
                throw new BusinessException("cart expired or deleted");
            }

            Optional<CacheCart> cacheCart = cacheCartRepository.findById(cacheStudent.getCartId());
            if (cacheCart.isPresent()) {
                CacheCart cart = cacheCart.get();
                
                // Delete all courses in cart
                cacheCourseRepository.deleteAllById(cart.getItemIds());
                
                // Delete cart
                cacheCartRepository.delete(cart);
            }
            
            // Clear cart reference from student
            cacheStudent.setCartId(null);
            cachestudentRepository.save(cacheStudent);
        }
        else{
            throw new ResourceNotFoundException("student not found");
        }
    }

}
