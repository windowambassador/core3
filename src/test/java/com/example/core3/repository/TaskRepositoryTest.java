package com.example.core3.repository;

import com.example.core3.entity.Priority;
import com.example.core3.entity.Task;
import com.example.core3.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User other;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User("owner", "owner@test.com", "pass"));
        other = userRepository.save(new User("other", "other@test.com", "pass"));
    }

    @Test
    void saveAndFindById() {
        Task task = taskRepository.save(new Task("Task 1", "Desc", Priority.MEDIUM, owner));

        Optional<Task> found = taskRepository.findById(task.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Task 1");
        assertThat(found.get().getUser().getId()).isEqualTo(owner.getId());
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc() {
        taskRepository.save(new Task("A", null, Priority.LOW, owner));
        taskRepository.save(new Task("B", null, Priority.HIGH, owner));
        taskRepository.save(new Task("C", null, Priority.MEDIUM, other));

        List<Task> ownerTasks = taskRepository.findByUserIdOrderByCreatedAtDesc(owner.getId());

        assertThat(ownerTasks).hasSize(2);
        assertThat(ownerTasks).allMatch(t -> t.getUser().getId().equals(owner.getId()));
    }

    @Test
    void findByIdAndUserId() {
        Task task = taskRepository.save(new Task("Mine", null, Priority.LOW, owner));

        assertThat(taskRepository.findByIdAndUserId(task.getId(), owner.getId())).isPresent();
        assertThat(taskRepository.findByIdAndUserId(task.getId(), other.getId())).isEmpty();
    }

    @Test
    void deleteTask() {
        Task task = taskRepository.save(new Task("Del", null, Priority.LOW, owner));
        Long id = task.getId();

        taskRepository.delete(task);

        assertThat(taskRepository.findById(id)).isEmpty();
    }

    @Test
    void countByUserId() {
        taskRepository.save(new Task("T1", null, Priority.LOW, owner));
        taskRepository.save(new Task("T2", null, Priority.LOW, owner));

        assertThat(taskRepository.countByUserId(owner.getId())).isEqualTo(2);
        assertThat(taskRepository.countByUserId(other.getId())).isZero();
    }
}
