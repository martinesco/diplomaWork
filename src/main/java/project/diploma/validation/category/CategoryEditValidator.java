package project.diploma.validation.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import project.diploma.domain.models.binding.CategoryAddBindingModel;
import project.diploma.domain.models.binding.CategoryEditBindingModel;
import project.diploma.repository.CategoryRepository;
import project.diploma.validation.ValidationConstants;
import project.diploma.validation.annotation.Validator;

@Validator
public class CategoryEditValidator implements org.springframework.validation.Validator {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryEditValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryEditBindingModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CategoryEditBindingModel categoryEditBindingModel = (CategoryEditBindingModel) o;

        //Category category = this.categoryRepository.findByName(categoryEditBindingModel.getName()).orElse(null);

        if (categoryEditBindingModel.getName().length() < 3) {
            errors.rejectValue(
                    "name",
                    ValidationConstants.NAME_LENGTH,
                    ValidationConstants.NAME_LENGTH
            );
        }

        if (this.categoryRepository.findByName(categoryEditBindingModel.getName()).isPresent()) {
            errors.rejectValue(
                    "name",
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryEditBindingModel.getName()),
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryEditBindingModel.getName())
            );
        }
    }
}