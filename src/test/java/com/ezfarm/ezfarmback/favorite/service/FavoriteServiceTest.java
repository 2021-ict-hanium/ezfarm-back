package com.ezfarm.ezfarmback.favorite.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ezfarm.ezfarmback.common.exception.CustomException;
import com.ezfarm.ezfarmback.common.exception.dto.ErrorCode;
import com.ezfarm.ezfarmback.farm.domain.Farm;
import com.ezfarm.ezfarmback.farm.domain.FarmRepository;
import com.ezfarm.ezfarmback.farm.domain.enums.CropType;
import com.ezfarm.ezfarmback.farm.domain.enums.FarmType;
import com.ezfarm.ezfarmback.favorite.domain.FavoriteRepository;
import com.ezfarm.ezfarmback.user.domain.Role;
import com.ezfarm.ezfarmback.user.domain.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("즐겨찾기 단위 테스트(Service)")
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FarmRepository farmRepository;

    private FavoriteService favoriteService;

    private User user1;

    private User user2;

    private Farm farm;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteService(farmRepository, favoriteRepository);
        user1 = User.builder()
            .name("남상우")
            .email("a@gmail.com")
            .password("비밀번호")
            .role(Role.ROLE_USER)
            .build();

        user2 = User.builder()
            .name("홍길동")
            .email("b@gmail.com")
            .password("비밀번호")
            .role(Role.ROLE_USER)
            .build();

        farm = Farm.builder()
            .address("경기")
            .phoneNumber("010-2222-2222")
            .area("100")
            .farmType(FarmType.GLASS)
            .cropType(CropType.PAPRIKA)
            .isMain(true)
            .startDate(null)
            .build();
    }

    @DisplayName("농장 즐겨찾기를 추가한다.")
    @Test
    void addFavorite_success() {
        //when
        when(farmRepository.findById(any())).thenReturn(Optional.ofNullable(farm));
        when(favoriteRepository.existsByUserAndFarm(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(any());

        favoriteService.addFavorite(user1, 1L);

        //then
        verify(favoriteRepository).save(any());
    }

    @DisplayName("즐겨찾기를 추가 시 존재하는 농장이 아니면 예외 처리한다.")
    @Test
    void addFavorite_failure_invalid_farm() {
        when(farmRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(user1, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_FARM_ID.getMessage());
    }

    @DisplayName("즐겨찾기 추가 시 중복되는 농장이면 예외 처리한다.")
    @Test
    void addFavorite_failure_duplicated_farm() {

        when(farmRepository.findById(any())).thenReturn(Optional.ofNullable(farm));
        when(favoriteRepository.existsByUserAndFarm(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(user1, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.FAVORITE_DUPLICATED.getMessage());
    }
}