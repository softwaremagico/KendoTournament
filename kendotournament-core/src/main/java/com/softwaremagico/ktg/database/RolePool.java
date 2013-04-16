package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.RoleTags;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RolePool extends TournamentDependentPool<Role> {

    private static RolePool instance;
    private static RoleTags roleTags = null;

    private RolePool() {
    }

    public static RolePool getInstance() {
        if (instance == null) {
            instance = new RolePool();
        }
        return instance;
    }

    @Override
    protected String getId(Role element) {
        return element.getCompetitor().getId() + element.getTournament().getName();
    }

    protected String getId(Tournament tournament, RegisteredPerson person) {
        return person.getId() + tournament.getName();
    }

    @Override
    protected HashMap<String, Role> getFromDatabase(Tournament tournament) {
        DatabaseConnection.getInstance().connect();
        List<Role> roles = DatabaseConnection.getInstance().getDatabase().getRoles(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Role> hashMap = new HashMap<>();
        for (Role role : roles) {
            hashMap.put(getId(role), role);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Role> elementsToStore) {
        if (elementsToStore.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().addRoles(elementsToStore);
        }
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Role> elementsToDelete) {
        if (elementsToDelete.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().removeRoles(tournament, elementsToDelete);
        }
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Role, Role> elementsToUpdate) {
        if (elementsToUpdate.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().updateRoles(tournament, elementsToUpdate);
        }
    }

    @Override
    protected List<Role> sort(Tournament tournament) {
        List<Role> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public Role getRole(Tournament tournament, RegisteredPerson participant) {
        for (Role role : getMap(tournament).values()) {
            if (role.getCompetitor().equals(participant)) {
                return role;
            }
        }
        return null;
    }

    public void setRegisteredPeopleInTournamentAsAccreditationPrinted(Tournament tournament) {
        for (Role role : getMap(tournament).values()) {
            if (!role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setRegisteredPeopleInTournamentAsAccreditationPrinted(Tournament tournament, List<RegisteredPerson> participants) {
        for (RegisteredPerson participant : participants) {
            Role role = getRole(tournament, participant);
            if (role != null && !role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public List<RegisteredPerson> getRegisteredPeopleInTournamenteWithoutAccreditation(Tournament tournament) {
        List<RegisteredPerson> results = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if (!role.isAccreditationPrinted()) {
                results.add(role.getCompetitor());
            }
        }
        Collections.sort(results);
        return results;
    }

    public void setRegisteredPeopleInTournamentAsDiplomaPrinted(Tournament tournament) {
        for (Role role : getMap(tournament).values()) {
            if (!role.isDiplomaPrinted()) {
                role.setDiplomaPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setRegisteredPeopleInTournamentAsDiplomaPrinted(Tournament tournament, List<RoleTag> rolesWithDiploma) {
        for (Role role : getMap(tournament).values()) {
            if (rolesWithDiploma.contains(role.getTag()) && !role.isDiplomaPrinted()) {
                role.setDiplomaPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public List<RegisteredPerson> getPeopleWithoutDiploma(Tournament tournament, List<RoleTag> rolesWithDiploma) {
        List<RegisteredPerson> results = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if ((rolesWithDiploma == null || rolesWithDiploma.contains(role.getTag())) && !role.isDiplomaPrinted()) {
                results.add(role.getCompetitor());
            }
        }
        Collections.sort(results);
        return results;
    }

    public List<RegisteredPerson> getPeople(Tournament tournament) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if (role.getCompetitor() != null) {
                result.add(role.getCompetitor());
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getPeople(Tournament tournament, String roleTag) {
        List<String> roles = new ArrayList<>();
        roles.add(roleTag);
        return getPeople(tournament, roles);
    }

    public List<RegisteredPerson> getPeople(Tournament tournament, List<String> roleTags) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if (roleTags.contains(role.getDatabaseTag())) {
                if (role.getCompetitor() != null) {
                    result.add(role.getCompetitor());
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getPeople(List<String> roleTags) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (Role role : getAll()) {
            if (roleTags.contains(role.getDatabaseTag())) {
                if (role.getCompetitor() != null) {
                    result.add(role.getCompetitor());
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public Integer getVolunteerOrder(Tournament tournament, RegisteredPerson person) {
        List<RegisteredPerson> volunteers = getPeople(tournament, RoleTag.volunteerRoles);
        for (int i = 0; i < volunteers.size(); i++) {
            if (volunteers.get(i).equals(person)) {
                return i;
            }
        }
        return 0;
    }

    public List<RegisteredPerson> getCompetitors(Tournament tournament) {
        List<RegisteredPerson> competitors = getPeople(tournament, RoleTag.competitorsRoles);
        return competitors;
    }

    public List<RegisteredPerson> getPeople(Tournament tournament, Club club) {
        List<RegisteredPerson> results = new ArrayList<>();
        List<RegisteredPerson> registeredPeople = getPeople(tournament);
        for (RegisteredPerson person : registeredPeople) {
            if (person.getClub().equals(club)) {
                results.add(person);
            }
        }
        return results;
    }

    public void remove(Tournament tournament, RegisteredPerson person) {
        List<Role> roles = get(tournament);
        for (Role role : roles) {
            if (role.getCompetitor().equals(person)) {
                remove(tournament, role);
            }
        }
    }

    public RoleTags getRoleTags() {
        if (roleTags == null) {
            roleTags = KendoTournamentGenerator.getInstance().getAvailableRoles();
        }
        return roleTags;
    }

    public void resetRoleTags() {
        roleTags = null;
    }
}
