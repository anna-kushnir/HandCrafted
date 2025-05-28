package annak.hc.dto;

import annak.hc.entity.User;
import lombok.Data;

@Data
public class NewGiftSetDto {
    private User user;
    private String packagingWishes;
}
