package com.Cts.service;
import com.Cts.entity.Receipt;
import com.Cts.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public Receipt saveReceipt(Receipt receipt){
        return receiptRepository.save(receipt);
    }
}
