package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service

public class PlayerServiceInt implements ServiceInt {
    private PlayerRepository playerRepository;

    @Autowired
    public void setRepository(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    public void paramsChecker(Player player){

if(player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12)){
    throw new BadRequestException("Player name is too long or absent");
}
if (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30)){
    throw new BadRequestException("Player title is too long or absent");
}
if (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000)){
    throw new BadRequestException("Player experience is out of range");
}
if (player.getBirthday() != null){
if (player.getBirthday().getTime() < 0) {
throw new BadRequestException("Invalid date");
}
    Calendar date = Calendar.getInstance();
    date.setTime(player.getBirthday());
    if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) {
        throw new BadRequestException("The date of ship manufacture is out of range");
    }}
   }


    private Integer calculateCurrentLevel(Player player){
        if(player.getExperience() == null){
            player.setExperience(0);
        }
       BigDecimal level = new BigDecimal((Math.sqrt(200 * player.getExperience()+2500) - 50) / 100);
      level = level.setScale(2, RoundingMode.HALF_UP);
return level.intValue();
       /* int exp = player.getExperience();
        return (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);*/
    }

    private Integer calculateUntilNextLevel(Player player){
        return (50 * (player.getLevel() + 1) * (player.getLevel() + 2)) - player.getExperience();
      /*  int exp = player.getExperience();
        int lvl = calculateCurrentLevel(player);
        return 50 * (lvl + 1) * (lvl + 2) - exp;*/
    }
    @Override
    public List<Player> getAllExistingPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getAllExistingPlayersList(Specification<Player> specification, Pageable sortedByName) {
        return playerRepository.findAll(specification,sortedByName);
    }

    @Override
    public Player createPlayer(Player playerRequired) {
        if(playerRequired.getName() == null
        || playerRequired.getTitle() == null
        || playerRequired.getRace() == null
        || playerRequired.getBirthday() == null
        || playerRequired.getProfession() == null
        || playerRequired.getExperience() == null
        ){
            throw new BadRequestException("Please fill in all required fields");
        }
        paramsChecker(playerRequired);
        if(playerRequired.isBanned() == null){
            playerRequired.setBanned(false);
        }
        playerRequired.setLevel(calculateCurrentLevel(playerRequired));
        playerRequired.setUntilNextLevel(calculateUntilNextLevel(playerRequired));
        return playerRepository.save(playerRequired);
    }

    @Override
    public Player updatePlayer(Long id, Player playerRequired) {
        paramsChecker(playerRequired);
if(!playerRepository.existsById(id)) throw new PlayerNotFoundException("Player is not found");
Player playerChanged = getPlayer(id);

if (playerRequired.getName() != null) playerChanged.setName(playerRequired.getName());
if (playerRequired.getTitle() != null) playerChanged.setTitle(playerRequired.getTitle());
if (playerRequired.getRace() != null) playerChanged.setRace(playerRequired.getRace());
if (playerRequired.getProfession() != null)playerChanged.setProfession(playerRequired.getProfession());
if (playerRequired.getBirthday() != null)playerChanged.setBirthday(playerRequired.getBirthday());
if (playerRequired.isBanned() != null) playerChanged.setBanned(playerRequired.isBanned());
if (playerRequired.getExperience() != null) playerChanged.setExperience(playerRequired.getExperience());

       setLevelAndExpUntilNextLevel(playerChanged);

        return playerRepository.save(playerChanged);
    }

    private void setLevelAndExpUntilNextLevel(Player player) {
        player.setLevel(calculateCurrentLevel(player));
        player.setUntilNextLevel(calculateUntilNextLevel(player));
    }
    @Override
    public void deleteByID(Long id) {
if (playerRepository.existsById(id)){
    playerRepository.deleteById(id);
} else throw new PlayerNotFoundException("Player not found");
    }


    @Override
    public Player getPlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player not found");
        }
        return playerRepository.findById(id).get();
    }

    @Override
    public Long idChecker(String id) {
        if (id == null || id.equals("0") || id.equals("")) {
            throw new BadRequestException("ID is incorrect");
        }
        try {
            Long iD = Long.parseLong(id);
            if(playerRepository.existsById(iD)){
            return iD;}
            else {
                throw new PlayerNotFoundException("Id not found");
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID is not a number", e);
        }
    }

    @Override
    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> playerRaceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> playerProfessionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> dateFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date before1 = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), after1);
            }

            Date before1 = new Date(before - 3600001);
            Date after1 = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), after1, before1);
        };
    }

    @Override
    public Specification<Player> bannedFilter(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) {
                return null;
            }
            if (isBanned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }

    @Override
    public Specification<Player> experienceFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> levelFilter(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return null;
            }
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }
}
