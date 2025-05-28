package annak.hc.mapper;

import annak.hc.entity.Color;
import annak.hc.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ColorMapper {

    private final ColorRepository colorRepository;

    public String colorsToString(Set<Color> colorsList) {
        StringBuilder colorsString = new StringBuilder();
        if (colorsList.isEmpty()) {
            return "";
        }
        for (Color color : colorsList) {
            colorsString.append(color.getName()).append(", ");
        }
        return colorsString.delete(colorsString.length() - 2, colorsString.length()).toString();
    }

    public Set<Color> colorsToList(String colorsString) {
        Set<Color> colorsList = new HashSet<>();
        for (String colorName : colorsString.toLowerCase().split(", ")) {
            if (colorRepository.existsByName(colorName)) {
                Color color = colorRepository.findByName(colorName);
                colorsList.add(color);
            } else {
                Color color = new Color();
                color.setName(colorName);
                colorsList.add(colorRepository.save(color));
            }
        }
        return colorsList;
    }
}
