package annak.hc.service;

import annak.hc.entity.Color;
import annak.hc.mapper.ColorMapper;
import annak.hc.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    @Override
    public List<Color> getAll() {
        return colorRepository.findAllByOrderByNameAsc();
    }

    public String convertColorsToString(Set<Color> colorsList) {
        return colorMapper.colorsToString(colorsList);
    }

    public Set<Color> convertColorsToList(String colorsString) {
        return colorMapper.colorsToList(colorsString);
    }

    @Override
    public Map<String, Long> getColorStatistics() {
        List<Object[]> statisticsList = colorRepository.getColorStatistics();
        Map<String, Long> statisticsMap = new LinkedHashMap<>();

        for (Object[] row : statisticsList) {
            statisticsMap.put((String)row[0], (Long)row[1]);
        }
        return statisticsMap;
    }
}
