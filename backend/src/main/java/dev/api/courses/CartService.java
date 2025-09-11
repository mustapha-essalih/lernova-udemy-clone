package dev.api.courses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.api.courses.model.Courses;
import dev.api.courses.model.MainCategories;
import dev.api.courses.model.Subcategories;
import dev.api.courses.model.redis.CacheCart;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.model.redis.CacheStudent;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CacheCartRepository;
import dev.api.courses.repository.redis.CacheCourseRepository;
import dev.api.courses.repository.redis.CacheStudentRepository;
import dev.api.courses.requests.AddToCartRequest;
import dev.api.courses.responses.CartCoursesResponse;
import dev.api.courses.responses.CartResponse;
import dev.api.exceptions.ResourceNotFoundException;
import dev.api.students.model.Students;
import dev.api.students.repository.StudentsRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CartService {

    private CacheStudentRepository cachestudentRepository;
    private CacheCartRepository cartRepository;
    private CoursesRepository coursesRepository;
    private CacheCourseRepository cacheCourseRepository;
    private StudentsRepository studentsRepository;

    // test add same course to cart
    //
    public void addToCart(AddToCartRequest request, String username) {

        Courses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
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

        // for (MainCategories mainCategorie : course.getMainCategories()) {
        // if(mainCategorie.getName() != null){
        // cacheCourse.getCategory().add(mainCategorie.getName());
        // }
        // for (Subcategories subcategorie : mainCategorie.getSubcategories()) {
        // cacheCourse.getCategory().add(subcategorie.getName());
        // }
        // }

        if (cacheStudent == null) {
            cacheStudent = new CacheStudent();
            cacheStudent.setStudentId("student_" + username);
            String cartId = "cart_" + UUID.randomUUID().toString();
            CacheCart cacheCart = new CacheCart();
            cacheCart.setCartId(cartId);
            cacheCart.setTotalAmount(course.getPrice());
            cacheStudent.setCartId(cartId);
            cachestudentRepository.save(cacheStudent);
            cacheCart.getItemIds().add("course_" + course.getCourseId());
            cartRepository.save(cacheCart);
            cacheCourseRepository.save(cacheCourse);
        } else {

            CacheCart cacheCart = cartRepository.findById(cacheStudent.getCartId()).get();
            if (!cacheCart.getItemIds().contains(cacheCourse.getId())) {
                cacheCart.setTotalAmount(cacheCart.getTotalAmount().add(course.getPrice()));
                cacheCart.getItemIds().add("course_" + course.getCourseId());
                cartRepository.save(cacheCart);
                cacheCourseRepository.save(cacheCourse);
            }

        }

    }

    public CartCoursesResponse getCart(String username) {

        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        if (cacheStudent == null) {
            return new CartCoursesResponse(null, 0, null);
        }

        CacheCart cacheCart = cartRepository.findById(cacheStudent.getCartId()).orElse(null);
        if (cacheCart == null) {
            return new CartCoursesResponse(null, 0, null);
        }

        Iterable<CacheCourse> cacheCourses = cacheCourseRepository.findAllById(cacheCart.getItemIds());

        return new CartCoursesResponse(cacheCart.getTotalAmount(), cacheCart.getItemIds().size(), cacheCourses);
    }

    public void removeItemFromCart(String courseId, String username) {

        CacheCourse course = cacheCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        if (cacheStudent != null) {
            CacheCart cacheCart = cartRepository.findById(cacheStudent.getCartId()).orElse(null);
            if (cacheCart != null) {
                cacheCart.getItemIds().remove(courseId);
                cacheCart.setTotalAmount(cacheCart.getTotalAmount().subtract(course.getPrice()));
                cartRepository.save(cacheCart);
                cacheCourseRepository.delete(course);
            }
        }

    }

    public void clearCart(String username) {
        CacheStudent cacheStudent = cachestudentRepository.findById("student_" + username).orElse(null);
        if (cacheStudent != null) {
            CacheCart cacheCart = cartRepository.findById(cacheStudent.getCartId()).orElse(null);
            if (cacheCart != null) {
                cartRepository.delete(cacheCart);
                cacheStudent.setCartId(null);
                cachestudentRepository.save(cacheStudent);
            }
        }
    
    }

}
