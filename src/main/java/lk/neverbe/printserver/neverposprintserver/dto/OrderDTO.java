package lk.neverbe.printserver.neverposprintserver.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDTO {
    private String orderId;
    private OrderItemDTO[] items;
    private String paymentMethod;
    private String discount;
    private PaymentDTO[] paymentReceived;
    private String createdAt;
}
