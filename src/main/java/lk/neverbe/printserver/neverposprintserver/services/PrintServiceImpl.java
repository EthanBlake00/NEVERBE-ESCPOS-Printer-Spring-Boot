package lk.neverbe.printserver.neverposprintserver.services;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lk.neverbe.printserver.neverposprintserver.dto.OrderDTO;
import lk.neverbe.printserver.neverposprintserver.dto.OrderItemDTO;
import lk.neverbe.printserver.neverposprintserver.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PrintServiceImpl implements PrintService {
    private final Logger logger = Logger.getLogger(PrintServiceImpl.class.getName());

    @Override
    public void printInvoice(OrderDTO orderDTO) throws IOException {
        javax.print.PrintService printService = PrinterOutputStream.getPrintServiceByName("XP-58IIH");
        PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
        EscPos escPos = new EscPos(printerOutputStream);

        // Define styles
        Style headerStyle = new Style().setBold(true).setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(Style.Justification.Center);
        Style defaultStyle = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(Style.Justification.Left_Default);
        Style rightAlignStyle = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(Style.Justification.Right);
        try {
            escPos.pulsePin(EscPos.PinConnector.Pin_2, 100, 100);
            escPos.feed(2);
            escPos.write(headerStyle, "NEVERBE\n");
            escPos.write(headerStyle, "New Kandy Road, Delgoda\n");
            escPos.write(headerStyle, "+9472624999 | +9470528999\n");
            escPos.write(headerStyle, "support@neverbe.lk\n");
            escPos.write(headerStyle, "--------------------------------\n");
            escPos.feed(1);

            // Print order details (Left Aligned)
            escPos.write(defaultStyle, "Date: " + orderDTO.getCreatedAt() + "\n");
            escPos.write(defaultStyle, "Order #: " + orderDTO.getOrderId().toUpperCase() + "\n");
            escPos.write(defaultStyle, "--------------------------------\n");

            // Print each item
            for (OrderItemDTO item : orderDTO.getItems()) {
                escPos.write(defaultStyle, item.getName() + " (" + item.getSize() + ")\n");
                escPos.write(defaultStyle, "  Qty: " + item.getQuantity() + " x Rs." + item.getPrice() +
                        " = Rs." + (item.getQuantity() * item.getPrice()) + "\n");
            }

            // Calculate totals
            double total = Arrays.stream(orderDTO.getItems()).mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();
            double discount = orderDTO.getDiscount() != null ? Double.parseDouble(orderDTO.getDiscount()) : 0;
            double subtotal = total - discount + orderDTO.getFee();
            double received = orderDTO.getPaymentReceived() != null ? Arrays.stream(orderDTO.getPaymentReceived()).mapToDouble(PaymentDTO::getAmount).sum() : 0;
            double change = subtotal - received;

            // Print totals (Right Aligned)
            escPos.write(defaultStyle, "--------------------------------\n");
            escPos.write(rightAlignStyle, "Total: Rs." + total + "\n");
            escPos.write(rightAlignStyle, "Fee: Rs." + orderDTO.getFee() + "\n");
            escPos.write(rightAlignStyle, "Discount: -Rs." + discount + "\n");
            escPos.write(rightAlignStyle.setJustification(EscPosConst.Justification.Right), "-----------------\n");
            escPos.write(rightAlignStyle, "Subtotal: Rs." + subtotal + "\n");
            escPos.write(rightAlignStyle, "Received: Rs." + received + "\n");
            escPos.write(rightAlignStyle.setJustification(EscPosConst.Justification.Right), "-----------------\n");
            escPos.write(rightAlignStyle, "Change: Rs." + change + "\n");
            escPos.write(defaultStyle, "--------------------------------\n");

            // Footer (Center Aligned)
            escPos.feed(1);
            escPos.write(headerStyle, "Thank you for shopping!\n");
            escPos.write(headerStyle, "Visit us again.\n");

            escPos.feed(1);
            // Seasonal Greetings
            LocalDate today = LocalDate.now();
            if (today.getMonthValue() == 12 && today.getDayOfMonth() != 31) {
                escPos.write(headerStyle.setJustification(EscPosConst.Justification.Center), "Merry Christmas!\n");
            } else if (today.getMonthValue() == 1 && today.getDayOfMonth() <= 10) {
                escPos.write(headerStyle.setJustification(EscPosConst.Justification.Center), "Happy New Year!\n");
            } else if (today.getMonthValue() == 4) {
                escPos.write(headerStyle.setJustification(EscPosConst.Justification.Center), "Happy Sinhala\nTamil New Year!\n");
            } else if (today.getMonthValue() == 2 && today.getDayOfMonth() == 14) {
                escPos.write(headerStyle.setJustification(EscPosConst.Justification.Center), "Happy Valentine's Day!\n");
            }

            escPos.feed(1);

            // Barcode
            BarCode barCode = new BarCode();
            barCode.setSystem(BarCode.BarCodeSystem.CODE39_A);
            barCode.setHRIPosition(BarCode.BarCodeHRIPosition.BelowBarCode);
            barCode.setBarCodeSize(2, 100);
            barCode.setJustification(EscPosConst.Justification.Center);

            // Print the barcode with the order ID
            escPos.write(barCode, orderDTO.getOrderId().toUpperCase());

            // Feed and cut
            escPos.feed(3);
            escPos.cut(EscPos.CutMode.FULL);
            escPos.close();
            logger.info("Invoice printed successfully");

        } catch (IOException e) {
            logger.severe("Failed to print invoice: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void openDrawer() {
        try {
            logger.info("Opening cash drawer");
            javax.print.PrintService printService = PrinterOutputStream.getPrintServiceByName("XP-58IIH");
            PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
            EscPos escPos = new EscPos(printerOutputStream);
            logger.info("Pulsing pin 2 for 100ms");

            escPos.pulsePin(EscPos.PinConnector.Pin_2, 100, 100);
            escPos.close();
            logger.info("Cash drawer opened successfully");
        } catch (Exception e) {
            logger.severe("Failed to open drawer: " + e.getMessage());
        }
    }
}
