package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Participant;
import com.softwaremagico.ktg.Role;
import com.softwaremagico.ktg.Tournament;
import java.util.HashMap;
import java.util.List;

public class RolePool extends TournamentDependentPool<Role> {

    @Override
    protected String getId(Role element) {
        return element.getCompetitor();
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
        DatabaseConnection.getConnection().getDatabase().addRoles(tournament, elementsToStore);
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

    public Role getRole(Tournament tournament, Participant participant) {
        for (Role role : get(tournament).values()) {
            if (role.getCompetitor().equals(participant.getId())) {
                return role;
            }
        }
        return null;
    }

    public void setParticipantsInTournamentAsAccreditationPrinted(Tournament tournament) {
        for (Role role : get(tournament).values()) {
            if (!role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setParticipantsInTournamentAsAccreditationPrinted(Tournament tournament, List<Participant> participants) {
        for (Participant participant : participants) {
            Role role = getRole(tournament, participant);
            if (role != null && !role.isAccreditationPrinted()) {
                role.setAccreditationPrinted(true);
                update(tournament, role, role);
            }
        }
    }

    public void setParticipantsInTournamentAsDiplomaPrinted(Tournament tournament) {
        for (Role role : get(tournament).values()) {
            if (!role.isDiplomaPrinted()) {
                role.setDiplomaPrinted(true);
                update(tournament, role, role);
            }
        }
    }
}
