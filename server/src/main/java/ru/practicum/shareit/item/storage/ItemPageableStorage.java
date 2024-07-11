package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemPageableStorage extends PagingAndSortingRepository<Item, Integer> {
    List<Item> findAllByUserId(Long userId, Pageable pageable);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(
            String name, String description, Pageable pageable);
}
