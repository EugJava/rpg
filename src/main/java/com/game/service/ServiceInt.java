package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ServiceInt {
        List<Player> getAllExistingPlayersList(Specification<Player> specification);
        Page<Player> getAllExistingPlayersList(Specification<Player> specification, Pageable sortedByName);
        Player createPlayer(Player playerRequired);
        Player updatePlayer(Long id, Player playerRequired);
        void deleteByID(Long id);
        Player getPlayer(Long id);
        Long idChecker(String id);
        Specification<Player> nameFilter(String name);
        Specification<Player> titleFilter(String title);
        Specification<Player> playerRaceFilter(Race race);
        Specification<Player> playerProfessionFilter(Profession profession);
        Specification<Player> dateFilter(Long after, Long before);
        Specification<Player> bannedFilter(Boolean isBanned);
        Specification<Player> experienceFilter(Integer min, Integer max);
        Specification<Player> levelFilter(Integer min, Integer max);

        }
