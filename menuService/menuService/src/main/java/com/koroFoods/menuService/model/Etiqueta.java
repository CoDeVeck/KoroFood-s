package com.koroFoods.menuService.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_ETIQUETA")
public class Etiqueta {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name="ID_ETIQUETA")
	private Integer idEtiqueta;
	
	@Column(name = "NOMBRE")
	private String nombre;
	
	@Column(name = "DESCRIPCION")
	private String descripcion;
	
    @Column(name="ACTIVO")
    private Boolean activo;
	
	@Column(name = "FECHA_REGISTRO")
	private LocalDateTime fechaRegistro;
}
