package dev.api.courses;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import dev.api.courses.model.Courses;
import dev.api.courses.model.Status;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.repository.CoursesRepository;
import dev.api.courses.repository.redis.CacheCourseRepository;
import dev.api.courses.requests.PaymentRequest;
import dev.api.courses.responses.PaymenetResponse;
import dev.api.exceptions.BadRequestException;
import dev.api.exceptions.BusinessException;
import dev.api.exceptions.InternalServerError;
import dev.api.exceptions.ResourceNotFoundException;
import dev.api.students.model.Order;
import dev.api.students.model.Students;
import dev.api.students.repository.OrderRepository;
import dev.api.students.repository.StudentsRepository;

@Service
public class PaymentService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${app.url.success}")
    private String successUrl;

    @Value("${app.url.cancel}")
    private String cancelUrl;

    @Value("${site.base.url.http}")
    private String frontendUrl;

    private StudentsRepository studentsRepository;
    private CoursesRepository coursesRepository;
    private OrderRepository orderRepository;
    private CacheCourseRepository cacheCourseRepository;
    private CartService cartService;

    
    public PaymentService(StudentsRepository studentsRepository, CoursesRepository coursesRepository,
            OrderRepository orderRepository, CacheCourseRepository cacheCourseRepository, CartService cartService) {
        this.studentsRepository = studentsRepository;
        this.coursesRepository = coursesRepository;
        this.orderRepository = orderRepository;
        this.cacheCourseRepository = cacheCourseRepository;
        this.cartService = cartService;
    }

    /**
     * Creates a Stripe checkout session for the given product request
     * 
     * @return Stripe response containing session details or error information
     */
    public PaymenetResponse checkoutProducts(PaymentRequest request, Integer userId) {
        
        Courses course = coursesRepository.findById(request.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("course not found"));
        
        if (!course.getStatus().equals(Status.PUBLISHED)) {
            throw new BusinessException("Course is not available for purchase");
        }

        try {
            Stripe.apiKey = secretKey;

            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                    .builder()
                    .setName(request.getCourseName())
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("USD")
                    .setUnitAmount(request.getAmount() * 100L) // Amount in cents
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel")
                    .putMetadata("studentid", userId.toString())
                    .putMetadata("courseId", request.getCourseId().toString())
                    .addLineItem(lineItem)
                    .build();

            Session session = Session.create(params);

            return new PaymenetResponse(session.getId(), session.getUrl());

        } catch (StripeException e) {

            throw new InternalServerError("An unexpected error occurred. Please try again later.");

        } catch (Exception e) {

            throw new InternalServerError("An unexpected error occurred. Please try again later.");
        }
    }

    public void paymentSuccessAndCreateOrder(String sessionId) {

        try {
            // Stripe.apiKey = secretKey;
            Session session = Session.retrieve(sessionId);
            if("complete".equals(session.getPaymentStatus())){
                Integer courseId = Integer.valueOf(session.getMetadata().get("courseId"));
                Integer studentid = Integer.valueOf(session.getMetadata().get("studentid"));

                Students student = studentsRepository.findById(studentid).get();
                Courses course = coursesRepository.findById(courseId).get();

                if (student.getCourses().contains(course)) {
                    throw new BadRequestException("Student already enrolled in this course");
                }

                Order order = new Order();

                Set<Courses> courses = new HashSet<>();
                courses.add(course);

                student.getCourses().add(course);
                
                course.getStudents().add(student);

                order.setCourses(courses);
                order.setPaid(true);
                order.setStudent(student);
                order.setTotalAmount(course.getPrice());

                studentsRepository.save(student);
                orderRepository.save(order);

                // need course id in cache with username
                 Optional<CacheCourse> cacheCourse = cacheCourseRepository.findById("course_" + courseId);
                if (!cacheCourse.isEmpty()) {
                    cartService.removeItemFromCart("course_" + courseId , student.getUsername());
                }

            }else{
                throw new BadRequestException("Payment verification failed");
            }
        
        } catch (StripeException e) {
            throw new BadRequestException("Payment verification failed");
        }
    }

}
