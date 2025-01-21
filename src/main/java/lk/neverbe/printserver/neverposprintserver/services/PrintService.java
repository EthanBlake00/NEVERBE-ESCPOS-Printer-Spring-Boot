package lk.neverbe.printserver.neverposprintserver.services;

import lk.neverbe.printserver.neverposprintserver.dto.OrderDTO;

import java.io.IOException;

public interface PrintService {
    void printInvoice(OrderDTO orderDTO) throws IOException;
    void openDrawer();
}
