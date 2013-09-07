package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.RoleTags;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.sql.SQLException;
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

    public List<RegisteredPerson> getCompetitors(Tournament tournament) throws SQLException {
        List<RegisteredPerson> competitors = getPeople(tournament, RoleTag.competitorsRoles);
        return competitors;
    }

    @Override
    protected HashMap<String, Role> getElementsFromDatabase(Tournament tournament) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        List<Role> roles = DatabaseConnection.getInstance().getDatabase().getRoles(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Role> hashMap = new HashMap<>();
        for (Role role : roles) {
            hashMap.put(getId(role), role);
        }
        return hashMap;
    }

    @Override
    protected String getId(Role element) {
        return element.getCompetitor().getId() + element.getTournament().getName();
    }

    protected String getId(Tournament tournament, RegisteredPerson person) {
        return person.getId() + tournament.getName();
    }

    public List<RegisteredPerson> getPeople(List<String> roleTags) throws SQLException {
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

    public List<RegisteredPerson> getPeople(Tournament tournament) throws SQLException {
        List<RegisteredPerson> result = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if (role.getCompetitor() != null) {
                result.add(role.getCompetitor());
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getPeople(Tournament tournament, Club club) throws SQLException {
        List<RegisteredPerson> results = new ArrayList<>();
        List<RegisteredPerson> registeredPeople = getPeople(tournament);
        for (RegisteredPerson person : registeredPeople) {
            if (person.getClub().equals(club)) {
                results.add(person);
            }
        }
        return results;
    }

    public List<RegisteredPerson> getPeople(Tournament tournament, List<String> roleTags) throws SQLException {
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

    public List<RegisteredPerson> getPeople(Tournament tournament, String roleTag) throws SQLException {
        List<String> roles = new ArrayList<>();
        roles.add(roleTag);
        return getPeople(tournament, roles);
    }

    public List<RegisteredPerson> getPeopleWithoutDiploma(Tournament tournament, List<RoleTag> rolesWithDiploma) throws SQLException {
        List<RegisteredPerson> results = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if ((rolesWithDiploma == null || rolesWithDiploma.contains(role.getTag())) && !role.isDiplomaPrinted()) {
                results.add(role.getCompetitor());
            }
        }
        Collections.sort(results);
        return results;
    }

    public List<RegisteredPerson> getRegisteredPeopleInTournamenteWithoutAccreditation(Tournament tournament) throws SQLException {
        List<RegisteredPerson> results = new ArrayList<>();
        for (Role role : getMap(tournament).values()) {
            if (!role.isAccreditationPrinted()) {
                results.add(role.getCompetitor());
            }
        }
        Collections.sort(results);
        return results;
    }

    public Role getRole(Tournament tournament, RegisteredPerson participant) throws SQLException {
        for (Role role : getMap(tournament).values()) {
            if (role.getCompetitor().equals(participant)) {
                return role;
            }
        }
        return null;
    }

    public RoleTags getRoleTags() {
        if (roleTags == null) {
            roleTags = KendoTournamentGenerator.getInstance().getAvailableRoles();
        }
        return roleTags;
    }

    public Integer getVolunteerOrder(Tournament tournament, RegisteredPerson person) throws SQLException {
        List<RegisteredPerson> volunteers = getPeople(tournament, RoleTag.volunteerRoles);
        for (int i = 0; i < volunteers.size(); i++) {
            if (volunteers.get(i).equals(person)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Remove all roles of a competitor of any tournament.
     *
     * @param person
     */
    public void remove(RegisteredPerson person) throws SQLException {
        List<Role> roles = getAll();
        for (Role role : roles) {
            if (role.getCompetitor().equals(person)) {
                remove(role.getTournament(), role);
            }
        }
    }

    /**
     * Removing a role must delete the team.
     *
     * @param tournament
     */
    @Override
    public boolean remove(Tournament tournament, List<Role> elements) throws SQLException {
        for (Role role : elements) {
            if (!remove(tournament, role)) {
                return false;
            }
        }
        return true;
    }

    public void remove(Tournament tournament, RegisteredPerson person) throws SQLException {
        List<Role> roles = get(tournament);
        for (Role role : roles) {
            if (role.getCompetitor().equals(person)) {
                remove(tournament, role);
            }
        }
    }

    /**
     * Removing a role must delete the team.
     *
     * @param tournament
     */
    @Override
    public boolean remove(Tournament tournament, Role element) throws SQLException {
        Team team = TeamPool.getInstance().get(tournament, element.getCompetitor());
        if (team != null) {
            TeamPool.getInstance().remove(tournament, team);
        }
        return super.remove(tournament, element);
    }

    /**
     * Removing a role must delete the team.
     *
     * @param tournament
     */
    @Override
    public boolean remove(Tournament tournament, String elementName) throws SQLException {
        return remove(tournament, getById(tournament, elementName));
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Role> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeRoles(tournament, elementsToDelete);
        }
        return true;
    }

    public void resetRoleTags() {
        roleTags = null;
    }

    public void setRegisteredPeopleInTournamentAsAccreditationPrinted(Tournament tournament) throws SQLException {
        List<Role> roles = new ArrayList(getMap(tournament).values());
        for (Role role : roles) {
            if (!role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setRegisteredPeopleInTournamentAsAccreditationPrinted(Tournament tournament, List<RegisteredPerson> participants) throws SQLException {
        for (RegisteredPerson participant : participants) {
            Role role = getRole(tournament, participant);
            if (role != null && !role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setRegisteredPeopleInTournamentAsDiplomaPrinted(Tournament tournament) throws SQLException {
        List<Role> roles = new ArrayList(getMap(tournament).values());
        for (Role role : roles) {
            if (!role.isDiplomaPrinted()) {
                role.setDiplomaPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setRegisteredPeopleInTournamentAsDiplomaPrinted(Tournament tournament, List<RoleTag> rolesWithDiploma) throws SQLException {
        List<Role> roles = new ArrayList(getMap(tournament).values());
        for (Role role : roles) {
            if (rolesWithDiploma.contains(role.getTag()) && !role.isDiplomaPrinted()) {
                role.setDiplomaPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    @Override
    protected List<Role> sort(Tournament tournament) throws SQLException {
        List<Role> unsorted = new ArrayList<>(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Role> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addRoles(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Role, Role> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateRoles(tournament, elementsToUpdate);
        }
        return true;
    }
}
