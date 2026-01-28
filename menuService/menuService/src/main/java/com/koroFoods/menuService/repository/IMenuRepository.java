package com.koroFoods.menuService.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.koroFoods.menuService.model.Plato;

public interface IMenuRepository extends JpaRepository<Plato, Integer> {
	@Query("""
			    SELECT P
			    FROM Plato P
			    ORDER BY
			        CASE P.tipoPlato
			            WHEN 'ENT' THEN 1
			            WHEN 'SEG' THEN 2
			            WHEN 'POS' THEN 3
			            WHEN 'BEB' THEN 4
			        END
			""")
	List<Plato> findAllByTipoPlato();
	}


