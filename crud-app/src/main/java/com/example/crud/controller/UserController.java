package com.example.crud.controller;

import com.example.crud.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> getAll() {
        return users;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        users.add(user);
        return user;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable int id, @RequestBody User user) {
        for (User u : users) {
            if (u.getId() == id) {
                u.setName(user.getName());
                return u;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        users.removeIf(u -> u.getId() == id);
        return "Deleted user " + id;
    }
}