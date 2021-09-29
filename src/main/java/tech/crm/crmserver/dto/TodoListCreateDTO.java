package tech.crm.crmserver.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import tech.crm.crmserver.common.enums.Status;
import tech.crm.crmserver.common.enums.ToDoListStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoListCreateDTO {

    @NotNull(message = "Date time could not be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;

    @NotNull(message = "Description could not be null")
    private String description;

    @NotNull(message = "Status could not be null")
    private ToDoListStatus status;
}
