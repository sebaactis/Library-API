package com.library.administration.controllers;

import com.library.administration.models.dto.WishListDTO;
import com.library.administration.models.entities.User;
import com.library.administration.models.entities.WishList;
import com.library.administration.services.WishListService;
import com.library.administration.services.implementation.UserService;
import com.library.administration.utilities.ApiResponse;

import org.apache.tomcat.util.http.parser.Authorization;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishListController {

    private final WishListService wishListService;
    private final UserService userService;

    public WishListController(WishListService wishListService, UserService userService) {
        this.wishListService = wishListService;
        this.userService = userService;
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<WishListDTO>> addBookToWishList(Authentication authentication, @PathVariable Long bookId) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            WishList wishListAddBook = wishListService.addBookToWishList(user.getId(), bookId);

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

    @GetMapping()
    public ResponseEntity<ApiResponse<List<WishListDTO>>> getWishListByUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            List<WishList> wishList = wishListService.getWishListByUser(user.getId());

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

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<String>> removeBookFromWishList(Authentication authentication, @PathVariable Long bookId) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            wishListService.removeBookFromWishList(user.getId(), bookId);
            ApiResponse<String> response = new ApiResponse<>("Book removed from the wish list successfully", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
