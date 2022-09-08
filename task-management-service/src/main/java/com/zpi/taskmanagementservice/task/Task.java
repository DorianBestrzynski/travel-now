package com.zpi.taskmanagementservice.task;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_sequence"
    )
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence", allocationSize = 10)
    @Column(unique = true, nullable = false)
    private Long groupId;

    @Column(name = "assignee", nullable = false, length = 10)
    private Long assignee;

    @Column(name = "group_id", nullable = false, length = 10)
    private Long group_id;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    public Task(Long assignee, Long group_id, String description) {
        this.assignee = assignee;
        this.group_id = group_id;
        this.description = description;
    }


}
