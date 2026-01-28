package com.koroFoods.qualificationService.repository;

import com.koroFoods.qualificationService.enums.TipoEntidad;
import com.koroFoods.qualificationService.model.Calificacion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICalificacionRepository extends JpaRepository<Calificacion, Integer> {
	List<Calificacion> findByIdUsuario(Integer idUsuario);
	boolean existsByIdUsuarioAndTipoEntidadAndIdEntidad(
	        Integer idUsuario,
	        TipoEntidad tipoEntidad,
	        Integer idEntidad
	);

}
