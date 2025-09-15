package dev.api.courses;

import java.security.Principal;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.api.common.ApiResponse;
import dev.api.courses.model.redis.CacheCart;
import dev.api.courses.model.redis.CacheCourse;
import dev.api.courses.model.redis.CacheStudent;
import dev.api.courses.requests.AddToCartRequest;
import dev.api.courses.responses.CartCoursesResponse;
import dev.api.courses.responses.CartResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
// @PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/v1/cart")
@RestController
public class CartController {

    private CartService cartService;
    


    @PostMapping("/items") // dont forget validation
    public ResponseEntity<ApiResponse<String>> addToCart(@Valid @RequestBody AddToCartRequest request, Principal principal) {
        this.cartService.addToCart(request , principal.getName());
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(new ApiResponse<String>(true, "course added to cart", null));
    }
   

    @GetMapping
    public ResponseEntity<ApiResponse<CartCoursesResponse>> getCart(Principal principal){

        CartCoursesResponse cart = this.cartService.getCart(principal.getName());

        return ResponseEntity.ok().body(new ApiResponse<CartCoursesResponse>(true, null, cart));
    }

    
    @DeleteMapping("/items/{courseId}")
    public ResponseEntity<ApiResponse<String>> removeItemFromCart(@PathVariable String courseId , Principal principal) {
        this.cartService.removeItemFromCart(courseId, principal.getName() );
        
        return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(new ApiResponse<String>(true, "course delted from cart", null));
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(Principal principal) {
        this.cartService.clearCart(principal.getName());
                return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(new ApiResponse<String>(true, "Cart cleared successfully.", null));
    }

}
