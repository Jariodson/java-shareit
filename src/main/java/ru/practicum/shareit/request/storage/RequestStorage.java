package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdNot(Long requesterId, Pageable pageable);

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    ItemRequest findByIdAndRequesterId(Long id, Long requesterId);
}
