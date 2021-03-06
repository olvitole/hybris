package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCaptureServiceResponseConverterTest {

    private static final String ORDER_CODE = "orderCode";
    private final BigDecimal expectedAmount = BigDecimal.valueOf(10.99d);

    @InjectMocks
    private WorldpayCaptureServiceResponseConverter testObj = new WorldpayCaptureServiceResponseConverter();
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CaptureServiceResponse captureServiceResponseMock;
    @Mock
    private Amount amountMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;

    @Before
    public void setUp() {
        testObj.setTargetClass(CaptureResult.class);

        when(captureServiceResponseMock.getAmount()).thenReturn(amountMock);
        when(captureServiceResponseMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(expectedAmount);
    }

    @Test
    public void convertShouldReturnCaptureResult() {
        final Currency currency = Currency.getInstance(Locale.UK);

        when(captureServiceResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());

        final CaptureResult result = testObj.convert(captureServiceResponseMock);

        assertEquals(currency, result.getCurrency());
        assertEquals(ORDER_CODE, result.getRequestId());
        assertNotNull(result.getRequestTime());
        assertEquals(expectedAmount, result.getTotalAmount());
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }
}
