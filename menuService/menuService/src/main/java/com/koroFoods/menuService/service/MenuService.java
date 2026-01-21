package com.koroFoods.menuService.service;

import com.koroFoods.menuService.repository.IMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final IMenuRepository menuRepository;
}
