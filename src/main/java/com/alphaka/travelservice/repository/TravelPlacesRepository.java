package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.TravelPlaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlacesRepository extends JpaRepository<TravelPlaces, Long> {
}
