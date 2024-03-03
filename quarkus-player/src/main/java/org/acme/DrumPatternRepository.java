package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

public class DrumPatternRepository implements PanacheRepository<DrumPattern> {

    public DrumPattern findByName(String name) {
        return find("name", name).firstResult();
    }
}
