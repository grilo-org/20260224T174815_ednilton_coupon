package com.outforce.coupon.infrastructure.web;

import com.outforce.coupon.application.create.CreateCouponResponse;
import com.outforce.coupon.application.create.CreateCouponUseCase;
import com.outforce.coupon.application.delete.DeleteCouponResponse;
import com.outforce.coupon.application.delete.DeleteCouponUseCase;
import com.outforce.coupon.application.get.GetCouponByIdUseCase;
import com.outforce.coupon.application.get.GetCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "Gerenciamento de cupons de desconto")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private final GetCouponByIdUseCase getCouponByIdUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase,
                            DeleteCouponUseCase deleteCouponUseCase,
                            GetCouponByIdUseCase getCouponByIdUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
        this.getCouponByIdUseCase = getCouponByIdUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Criar cupom",
            description = "Cria um novo cupom. Caracteres especiais no código são removidos automaticamente, mantendo exatamente 6 caracteres alfanuméricos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campo obrigatório ausente ou formato inválido", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (data no passado, desconto abaixo do mínimo, código inválido)", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<CreateCouponResponse> create(@RequestBody @Valid CouponRequest request) {
        CreateCouponResponse response = createCouponUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cupom por ID",
            description = "Retorna os dados de um cupom pelo seu identificador único."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupom encontrado"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<GetCouponResponse> getById(
            @Parameter(description = "ID do cupom", example = "df7ddff3-03f5-4362-9bf5-5a5a8ce47b93")
            @PathVariable UUID id
    ) {
        GetCouponResponse response = getCouponByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar cupom",
            description = "Realiza soft delete do cupom. O registro é mantido no banco com status DELETED. Não é possível deletar um cupom já deletado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "409", description = "Cupom já foi deletado anteriormente", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<DeleteCouponResponse> delete(
            @Parameter(description = "ID do cupom", example = "df7ddff3-03f5-4362-9bf5-5a5a8ce47b93")
            @PathVariable UUID id
    ) {
        DeleteCouponResponse response = deleteCouponUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}