package com.library.administration.controllers;

import com.library.administration.models.dto.WishListDTO;
import com.library.administration.models.entities.WishList;
import com.library.administration.services.WishListService;
import com.library.administration.utilities.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishListController {
    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<ApiResponse<WishListDTO>> addBookToWishList(@PathVariable Long userId, @PathVariable Long bookId) {
        try {
            WishList wishListAddBook = wishListService.addBookToWishList(userId, bookId);

            if (wishListAddBook == null) {
                ApiResponse<WishListDTO> response = new ApiResponse<>("Cannot add book to the wish list", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();
            WishListDTO wishListDTO = modelMapper.map(wishListAddBook, WishListDTO.class);

            ApiResponse<WishListDTO> response = new ApiResponse<>("Book added to wish list successfully", wishListDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<WishListDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<WishListDTO>>> getWishListByUser(@PathVariable Long userId) {
        try {
            List<WishList> wishList = wishListService.getWishListByUser(userId);

            if (wishList == null) {
                ApiResponse<List<WishListDTO>> response = new ApiResponse<>("Wish list not found", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ModelMapper modelMapper = new ModelMapper();

            List<WishListDTO> wishListDTO = wishList.stream()
                    .map(wish -> modelMapper.map(wish, WishListDTO.class))
                    .toList();

            ApiResponse<List<WishListDTO>> response = new ApiResponse<>("Wish List found!", wishListDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            ApiResponse<List<WishListDTO>> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<ApiResponse<String>> removeBookFromWishList(@PathVariable Long userId, @PathVariable Long bookId) {
        try {
            wishListService.removeBookFromWishList(userId, bookId);
            ApiResponse<String> response = new ApiResponse<>("Book removed from the wish list successfully", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
