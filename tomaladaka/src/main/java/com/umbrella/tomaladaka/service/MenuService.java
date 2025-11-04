package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.model.Item;
import com.umbrella.tomaladaka.model.Menu;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.repository.ItemRepository;
import com.umbrella.tomaladaka.repository.MenuRepository;
import com.umbrella.tomaladaka.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final MenuRepository menuRepo;
    private final RestaurantRepository restaurantRepo;
    private final ItemRepository itemRepo;

    public MenuService(MenuRepository menuRepo, RestaurantRepository restaurantRepo, ItemRepository itemRepo) {
        this.menuRepo = menuRepo;
        this.restaurantRepo = restaurantRepo;
        this.itemRepo = itemRepo;
    }

    public Menu addItemToRestaurantMenu(Long restaurantId, Long itemId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        Menu menu = restaurant.getMenu();
        if (menu == null) {
            throw new IllegalStateException("Restaurant don't have a menu associated.");
        }

        if (!menu.getItems().contains(item)) {
            menu.getItems().add(item);
        }
        return menuRepo.save(menu);
    }
}