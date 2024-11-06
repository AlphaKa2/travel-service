package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.entity.TravelPlaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlacesRepository extends JpaRepository<TravelPlaces, Long> {
}
