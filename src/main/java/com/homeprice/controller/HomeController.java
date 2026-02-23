package com.homeprice.controller;

import com.homeprice.model.domain.PropertyInput;
import com.homeprice.service.ModelTrainer;
import com.homeprice.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC Controller for home page and prediction form.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private final ModelTrainer modelTrainer;
    private final PredictionService predictionService;

    public HomeController(ModelTrainer modelTrainer, PredictionService predictionService) {
        this.modelTrainer = modelTrainer;
        this.predictionService = predictionService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("propertyInput", new PropertyInput());
        List<String> locations = modelTrainer.getLocationOrder();
        if (locations.isEmpty()) {
            locations = List.of("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Pune", "Kolkata");
        }
        model.addAttribute("locations", locations);
        return "index";
    }

    @PostMapping("/predict")
    public String predict(@Valid PropertyInput propertyInput, BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        List<String> locations = modelTrainer.getLocationOrder();
        if (locations.isEmpty()) {
            locations = List.of("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Pune", "Kolkata");
        }
        model.addAttribute("locations", locations);

        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyInput", propertyInput);
            return "index";
        }

        long predictedPrice = predictionService.predictPriceInr(propertyInput);
        String formattedPrice = predictionService.formatPriceInr(predictedPrice);
        redirectAttributes.addFlashAttribute("predictedPrice", predictedPrice);
        redirectAttributes.addFlashAttribute("formattedPrice", formattedPrice);
        redirectAttributes.addFlashAttribute("propertyInput", propertyInput);
        return "redirect:/result";
    }

    @GetMapping("/result")
    public String result(Model model) {
        if (!model.containsAttribute("formattedPrice")) {
            return "redirect:/";
        }
        return "result";
    }
}
