package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.ServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ControllerClass {
private ServiceInt serviceInt;

@Autowired
    public void setService(ServiceInt serviceInt) {
        this.serviceInt = serviceInt;
    }
@GetMapping("/players")
@ResponseStatus(HttpStatus.OK)
public List<Player> getPlayersList(
        @RequestParam(value = "name",required = false) String name,
        @RequestParam(value = "title",required = false)String title,
        @RequestParam(value = "race",required = false)Race race,
        @RequestParam(value = "profession",required = false)Profession profession,
        @RequestParam(value = "after",required = false)Long after,
        @RequestParam(value = "before",required = false)Long before,
        @RequestParam(value = "banned",required = false)Boolean banned,
        @RequestParam(value = "minExperience",required = false)Integer minExperience,
        @RequestParam(value = "maxExperience",required = false)Integer maxExperience,
        @RequestParam(value = "minLevel",required = false)Integer minLevel,
        @RequestParam(value = "maxLevel",required = false)Integer maxLevel,
        @RequestParam(value = "order",required = false)PlayerOrder order,
        @RequestParam(value = "pageNumber",required = false)Integer pageNumber,
        @RequestParam(value = "pageSize",required = false)Integer pageSize
        ){
    if (pageNumber == null){
        pageNumber = 0;
    }
if(pageSize == null){
    pageSize = 3;
}

    PageRequest pageable;
    if(order == null){
        PlayerOrder order1 = PlayerOrder.ID;
         pageable = PageRequest.of(pageNumber,pageSize, Sort.by(order1.getFieldName()));
    }
    else {
        pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
    }
    return serviceInt.getAllExistingPlayersList(
            Specification.where(
                    serviceInt.nameFilter(name)
                            .and(serviceInt.titleFilter(title)))
                    .and(serviceInt.playerRaceFilter(race))
                    .and(serviceInt.playerProfessionFilter(profession))
                    .and(serviceInt.dateFilter(after,before))
                    .and(serviceInt.bannedFilter(banned))
                    .and(serviceInt.experienceFilter(minExperience,maxExperience))
                    .and(serviceInt.levelFilter(minLevel,maxLevel)),pageable).getContent();

}
@GetMapping("/players/count")
@ResponseStatus(HttpStatus.OK)
public Integer getPlayersCount(
        @RequestParam(value = "name",required = false) String name,
        @RequestParam(value = "title",required = false)String title,
        @RequestParam(value = "race",required = false)Race race,
        @RequestParam(value = "profession",required = false)Profession profession,
        @RequestParam(value = "after",required = false)Long after,
        @RequestParam(value = "before",required = false)Long before,
        @RequestParam(value = "banned",required = false)Boolean banned,
        @RequestParam(value = "minExperience",required = false)Integer minExperience,
        @RequestParam(value = "maxExperience",required = false)Integer maxExperience,
        @RequestParam(value = "minLevel",required = false)Integer minLevel,
        @RequestParam(value = "maxLevel",required = false)Integer maxLevel

){
return serviceInt.getAllExistingPlayersList(
        Specification.where(serviceInt.nameFilter(name)
                .and(serviceInt.titleFilter(title)))
                .and(serviceInt.playerRaceFilter(race))
                .and(serviceInt.playerProfessionFilter(profession))
                .and(serviceInt.dateFilter(after, before))
                .and(serviceInt.bannedFilter(banned))
                .and(serviceInt.experienceFilter(minExperience,maxExperience))
                .and(serviceInt.levelFilter(minLevel,maxLevel))).size();

}
@PostMapping("/players")
@ResponseStatus(HttpStatus.OK)
public Player createPlayer(@RequestBody Player player){
return serviceInt.createPlayer(player);
}


    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable("id")String id){
    Long iD = serviceInt.idChecker(id);
    return serviceInt.getPlayer(iD);
}
@PostMapping("/players/{id}")
@ResponseStatus(HttpStatus.OK)
public Player updatePlayer(@PathVariable("id")String id,@RequestBody Player player){
    Long iD = serviceInt.idChecker(id);
    return serviceInt.updatePlayer(iD, player);
}
@DeleteMapping("/players/{id}")
@ResponseStatus(HttpStatus.OK)
public void deletePlayer(@PathVariable("id")String id){
Long iD = serviceInt.idChecker(id);
 serviceInt.deleteByID(iD);
}
}
