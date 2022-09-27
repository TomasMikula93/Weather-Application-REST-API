package com.example.weatherapplicationrestapi.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "waUser")
    private WAUser waUser;

    @OneToMany(mappedBy = "userList", cascade = CascadeType.ALL)
    private List<City> favouriteCities;

    public UserList(WAUser waUser, List<City> favouriteCities) {
        this.waUser = waUser;
        this.favouriteCities = favouriteCities;
    }
}