package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.facades.WorldpayCartFacade;
import com.worldpay.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/checkout/multi/worldpay/redirect")
public class WorldpayRedirectCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    @Resource
    private WorldpayCartFacade worldpayCartFacade;

    /**
     * Validates and saves form details on submit of the payment details form for the HOP payment flow and redirects to Worldpay payment pages
     *
     * @param model
     * @param paymentDetailsForm
     * @param bindingResult
     * @param redirectAttrs
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/add-payment-details", method = POST)
    @RequireHardLogIn
    public String addPaymentDetails(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult, final RedirectAttributes redirectAttrs)
            throws CMSItemNotFoundException {

        getPaymentDetailsFormValidator().validate(paymentDetailsForm, bindingResult);

        if (addGlobalErrors(model, bindingResult)) {
            return handleFormErrors(model, paymentDetailsForm);
        }
        handleAndSaveAddresses(paymentDetailsForm);

        final String shopperBankCode = paymentDetailsForm.getShopperBankCode();
        worldpayCartFacade.resetDeclineCodeAndShopperBankOnCart(shopperBankCode);

        final String paymentMethod = paymentDetailsForm.getPaymentMethod();
        if (isAPM(paymentMethod)) {
            redirectAttrs.addFlashAttribute(SHOPPER_BANK_CODE, shopperBankCode);
        }
        redirectAttrs.addFlashAttribute(SAVE_PAYMENT_INFO, paymentDetailsForm.getSaveInAccount());
        redirectAttrs.addFlashAttribute(PAYMENT_METHOD_PARAM, paymentDetailsForm.getPaymentMethod());
        return getRedirectToPaymentMethod();
    }

}
