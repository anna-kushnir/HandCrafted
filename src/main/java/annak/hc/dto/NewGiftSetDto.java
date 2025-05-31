package annak.hc.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewGiftSetDto {
    private List<NewGiftSetItemDto> items;
    private String packagingWishes;
}
