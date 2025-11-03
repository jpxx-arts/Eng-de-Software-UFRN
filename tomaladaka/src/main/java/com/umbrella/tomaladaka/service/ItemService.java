package com.umbrella.tomaladaka.service;
import com.umbrella.tomaladaka.model.Item;
import com.umbrella.tomaladaka.repository.ItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepo;
    public ItemService(ItemRepository itemRepo) { this.itemRepo = itemRepo; }
    public Item createItem(Item item) { return itemRepo.save(item); }
    public List<Item> getAllItems() { return itemRepo.findAll(); }
}