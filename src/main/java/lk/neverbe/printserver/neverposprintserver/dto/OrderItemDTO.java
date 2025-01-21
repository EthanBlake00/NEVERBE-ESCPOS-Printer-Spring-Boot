package lk.neverbe.printserver.neverposprintserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OrderItemDTO {
   private String itemId;
   private String variantId;
   private String name;
   private String variantName;
   private String size;
   private int quantity;
   private double price;
   private double discount;
}
