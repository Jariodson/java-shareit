package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static BookingState fromStringIgnoreCase(String data) {
		if (data != null) {
			for (BookingState sortType : BookingState.values()) {
				if (data.equalsIgnoreCase(sortType.toString())) {
					return sortType;
				}
			}
		}
		throw new IllegalArgumentException(String.format("Неизвестный state: %s", data));
	}
}
