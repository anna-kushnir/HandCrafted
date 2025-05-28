package annak.hc.controller.admin;

import annak.hc.service.CategoryService;
import annak.hc.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final CategoryService categoryService;
    private final ColorService colorService;

    @GetMapping
    public String getStatistics(Model model) {
        model.addAttribute("categoryStatistics", categoryService.getCategoryStatistics());
        model.addAttribute("colorStatistics", colorService.getColorStatistics());
        return "admin/statistics";
    }
}
