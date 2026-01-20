package com.yesul.alcohol.controller;

import com.yesul.alcohol.model.dto.AlcoholDetailDto;
import com.yesul.alcohol.model.dto.AlcoholDto;
import com.yesul.alcohol.service.AlcoholService;
import com.yesul.util.ImageUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/alcohols")
public class AdminAlcoholController {

    private final AlcoholService alcoholService;
    private final ImageUpload imageUpload;

    private static final String DOMAIN = "alcohol";

    @GetMapping
    public String alcoholMgmtPage(@PageableDefault(size = 12) Pageable pageable, Model model) {
        Page<AlcoholDto> alcoholListPageable = alcoholService.getAlcoholList(pageable);
        model.addAttribute("alcoholList", alcoholListPageable);
        return "admin/alcohol/list";
    }

    @GetMapping("/{id}")
    public String getAlcoholDetail(@PathVariable Long id, Model model) {
        AlcoholDetailDto alcohol = alcoholService.getAlcoholDetailById(id);

        Map<String, Integer> tasteLevels = new LinkedHashMap<>();
        tasteLevels.put("단맛", alcohol.getSweetnessLevel());
        tasteLevels.put("산미", alcohol.getAcidityLevel());
        tasteLevels.put("바디감", alcohol.getBodyLevel());
        tasteLevels.put("향", alcohol.getAromaLevel());
        tasteLevels.put("떫은맛", alcohol.getTanninLevel());
        tasteLevels.put("여운", alcohol.getFinishLevel());
        tasteLevels.put("탄산감", alcohol.getSparklingLevel());

        model.addAttribute("alcohol", alcohol);
        model.addAttribute("tasteLevels", tasteLevels);
        return "admin/alcohol/detail";
    }

    @GetMapping("/regist")
    public String registPage(Model model) {
        model.addAttribute("alcohol", new AlcoholDetailDto());
        return "admin/alcohol/alcohol-form";
    }

    @PostMapping("/regist")
    public String registAlcohol(@ModelAttribute AlcoholDetailDto alcoholDetailDto, MultipartFile imageFile) {

        String url = imageUpload.uploadAndGetUrl(DOMAIN, imageFile);
        alcoholDetailDto.setImage(url);
        alcoholService.registAlcohol(alcoholDetailDto);

        return "redirect:/admin/alcohols";
    }

    @GetMapping("/edit/{id}")
    public String editAlcohol(@PathVariable Long id, Model model) {
        AlcoholDetailDto alcohol = alcoholService.getAlcoholDetailById(id);
        model.addAttribute("alcohol", alcohol);
        return "admin/alcohol/alcohol-form";
    }

    @PostMapping("/edit")
    public String updateAlcohol(@ModelAttribute AlcoholDetailDto alcohol, MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            if (alcohol.getImage() != null) {
                imageUpload.delete(alcohol.getImage());
            }
            String url = imageUpload.uploadAndGetUrl(DOMAIN, imageFile);
            alcohol.setImage(url);
        }
        alcoholService.updateAlcohol(alcohol);
        return "redirect:/admin/alcohols";
    }

    @GetMapping("/delete/{id}")
    public String deleteAlcohol(@PathVariable Long id) {
        AlcoholDetailDto alcohol = alcoholService.getAlcoholDetailById(id);

        imageUpload.delete(alcohol.getImage());
        alcoholService.deleteAlcoholById(id);

        return "redirect:/admin/alcohols";
    }
}
