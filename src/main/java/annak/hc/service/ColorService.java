package annak.hc.service;

import annak.hc.entity.Color;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ColorService {
    List<Color> getAll();
    String convertColorsToString(Set<Color> colorsList);
    Set<Color> convertColorsToList(String colorsString);
    Map<String, Long> getColorStatistics();
}
