package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.RegisteredPerson;
import com.softwaremagico.ktg.Role;
import com.softwaremagico.ktg.RoleTag;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RolePool extends TournamentDependentPool<Role> {

    private static RolePool instance;

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
        List<Role> roles = DatabaseConnection.getInstance().getDatabase().getRoles(tournament);
        HashMap<String, Role> hashMap = new HashMap<>();
        for (Role role : roles) {
            hashMap.put(getId(role), role);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Role> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addRoles(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Role> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeRoles(tournament, elementsToDelete);
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Role, Role> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateRoles(tournament, elementsToUpdate);
    }

    @Override
    protected List<Role> sort(Tournament tournament) {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
