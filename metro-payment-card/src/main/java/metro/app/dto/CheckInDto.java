package metro.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import metro.app.constants.Category;
import metro.app.constants.Destination;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDto {

 private Category category;
 
 private Destination destination;

}