package dev.api.courses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import dev.api.authentication.model.BaseEntity;
import dev.api.common.ApiResponse;
import dev.api.courses.requests.PaymentRequest;
import dev.api.courses.responses.PaymenetResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/api/v1/payment")
@AllArgsConstructor
@RestController
public class Payment {

    private PaymentService paymentService;


    // when pay a course remove it from cart if found


    @PostMapping("/create-checkout-session")
    public ResponseEntity<ApiResponse<PaymenetResponse>> createCheckoutSession(
            @Valid @RequestBody PaymentRequest request , @AuthenticationPrincipal BaseEntity user) {
                
        PaymenetResponse response = paymentService.checkoutProducts(request , user.getId());
        
            return ResponseEntity.ok(new ApiResponse<PaymenetResponse>(true, null, response));
    }

 
 
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> paymentSuccessAndCreateOrder(@RequestParam String sessionId) {
        

        paymentService.paymentSuccessAndCreateOrder(sessionId);

        
        return null;
    }

    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> paymentCancel() {
        return ResponseEntity.ok(new ApiResponse<String>(true, "Payment was cancelled. You can try again anytime." , null));
    }
}