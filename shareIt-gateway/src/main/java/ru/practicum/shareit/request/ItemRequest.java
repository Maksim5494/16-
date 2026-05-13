package ru.practicum.shareit.request;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @CreationTimestamp
    private LocalDateTime created;

    public ItemRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public User getRequester() {
        return requester;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
