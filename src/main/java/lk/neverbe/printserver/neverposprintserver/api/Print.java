package lk.neverbe.printserver.neverposprintserver.api;

import com.github.anastaciocintra.output.PrinterOutputStream;
import lk.neverbe.printserver.neverposprintserver.dto.OrderDTO;
import lk.neverbe.printserver.neverposprintserver.services.PrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/printer")
@RequiredArgsConstructor
public class Print {
    private final Logger logger = Logger.getLogger(Print.class.getName());
    private final PrintService printService;

    @PostMapping("/print")
    public ResponseEntity<String> printInvoice(@RequestBody OrderDTO orderDTO) {
        try {
            logger.info("Printing invoice for order: " + orderDTO.getOrderId());
            printService.printInvoice(orderDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.severe(e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/drawer")
    public ResponseEntity<String> openDrawer() {
        try {
            logger.info("Opening cash drawer");
            printService.openDrawer();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.severe(e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
