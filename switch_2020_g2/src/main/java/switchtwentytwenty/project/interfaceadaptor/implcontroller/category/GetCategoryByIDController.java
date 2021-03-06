package switchtwentytwenty.project.interfaceadaptor.implcontroller.category;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import switchtwentytwenty.project.applicationservice.appservice.iappservice.ICategoryService;
import switchtwentytwenty.project.dto.outdto.CategoryOutDTO;
import switchtwentytwenty.project.exception.ElementNotFoundException;
import switchtwentytwenty.project.interfaceadaptor.icontroller.category.IGetCategoryByIDController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class GetCategoryByIDController implements IGetCategoryByIDController {

    //Attributes

    @Autowired
    private final ICategoryService categoryService;

    //Business Methods

    /**
     * Get category by identifier.
     *
     * @param categoryID category identifier
     * @return category dto
     */
    @GetMapping(value = "/categories/{categoryID}")
    public ResponseEntity<Object> getCategoryByID(@PathVariable(value = "categoryID") String categoryID) throws ElementNotFoundException {
        CategoryOutDTO dto = categoryService.getCategoryByID(categoryID);
        Link selfLink = linkTo(methodOn(GetCategoryByIDController.class).getCategoryByID(categoryID)).withRel("self");
        dto.add(selfLink);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
