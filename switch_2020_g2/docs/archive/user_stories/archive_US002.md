# US002 - Get Standard Categories Tree 
=======================================

# 1. Requirements

> __"As a system manager, I want to get the standard categories tree."__

* The System Manager has the possibility of getting a standard category tree. These standard categories will be same
in all the families and should exist when creating one.


**Demo1** The system manager can obtain the standard categories tree of the application.

- Demo1.1. The standard categories tree will be the same in all the families.

- Demo1.2. The tree of standard categories cannot be modified by any of the family members.


# 2. Analysis
## 2.1 Product Owner
* Some answers of the product owner (PO) are important in some design decisions.
> It's not possible to add a category that already exists in the tree.

> A new root category can be added at anytime by the system manager.


## 2.2 Decisions

- The categories on the list are represented by its categoryDesignation, meaning the list will be filled with Strings and not references to the instance Category.
- For the user, the main identifier will be the categoryDesignation of the category, however in the system each category has is full path 
that works as the main identifier of the category. 

## 2.3 Dependent US
- The [US001](./US001.md) influences the implementation of this user storie and the [US002](./US002.md) 
is dependent of this US.
- The [US001](./US001.md) defines how the categories are going to be created and added to Category Service, and that influences 
how to get the tree of categories.

# 3. Design

## 3.1. Functionalities Flow

```puml
autonumber
skinparam monochrome true

title Get Standard Categories Tree
actor "System Manager" as SM
participant UI
participant ":GetStandard\nCategoriesTreeController" as SCTC
participant ":FFMApplication" as App
participant "categoryService:\nCategoryService" as Cs
participant "categories:List<Category>" as lc
participant "newList:List<Category>" as nl

SM -> UI : request standard\ncategories tree
activate UI
UI -> SCTC : getStandardCategoriesTree()
deactivate UI

activate SCTC
SCTC -> App : getCategoryService()
activate App 
SCTC <-- App : categoryService
deactivate App 

SCTC -> Cs : getStandardCategoriesTree()
activate Cs

loop for each Category in categories
    Cs -> lc : findCategory
    opt (!category.hasParent())
      lc --> Cs : rootCategory
      Cs -> nl : addRootCategory
    end
end

SCTC <-- Cs : List of categories
deactivate Cs
activate UI
UI <-- SCTC : List of categories
deactivate SCTC
deactivate UI
SM <-- UI : List of categories

```

* The GetStandardCategoriesTreeController will invoke the CategoryService that stores a list of Categories already present
in the application from whom it will then get the ones that are root categories (categories where parent is null). We
only need the root categories because a Category contains his children as attributes and with that we can recursively
access the full tree of categories.

## 3.2. Class Diagram

```puml
skinparam monochrome true

class Category {
    - categoryDesignation : String
    - children : List<Category>
    - parent: Category
    + getChildren()
}

class CategoryService {
    + getStandardCategoriesTree()
  
}

class GetStandardCategoriesTreeController {
    + getStandardCategoriesTree()
}

class FFMApplication {

}

GetStandardCategoriesTreeController - FFMApplication :categoryService
FFMApplication "1" *-- "1" CategoryService :categoryList
CategoryService "1" *-- "0..*" Category 
GetStandardCategoriesTreeController - CategoryService

```

* As shown in the sequence diagram there is the class Controller that makes the connection between the UI and the business
logic. The main functionality of the FFMApplication is to delegate the incoming requests to the appropriate Services,in this
case, CategoryService which contains the list of all the categories.

## 3.3. Applied Design Patterns

* From GRASP pattern:
Controller, Information Expert, Low Coupling

* From SOLID:
Single Responsibility Principle

## 3.4. Tests

**Test 1:** Returns the list with the root categories

	@Test
    public void getStandardCategoriesTree() { 
        ArrayList<Category> rootCategories;
        CategoryService categoryService = new CategoryService();
        Category categoryOne = categoryService.createCategory("Health");
        categoryService.createChildCategory("Medication", "Health");
        Category categoryTwo = categoryService.createCategory("Food");
        categoryService.createChildCategory("Pet's Food", "Food");
        categoryService.createChildCategory("Groceries", "Food");
        Category categoryThree = categoryService.createCategory("Transportation");
        rootCategories = categoryService.getStandardCategoriesTree();
        Assertions.assertEquals(3, rootCategories.size());
        Assertions.assertEquals(1,categoryOne.getChildren().size());
        Assertions.assertEquals(2,categoryTwo.getChildren().size());
        Assertions.assertEquals(0,categoryThree.getChildren().size());
	}


	
Tests the creation of three root categories and adds one child to one of them, two children categories to another and the 
last root category doesn't have a child.
Using the size of the list of children we can check if the child categories are added to the list.

# 4. Implementation

* The implementation of this US, was quite simple because of the way of how [US001](US001.md) is implemented in the switchtwentytwenty.
*Test 1*, is a good example of that. 

# 5. Integration/Demo

* Given the implementation of [US001](US001.md), the only thing necessary to implement was the iteration of the list of categories
in order to see which ones where root categories, and the addition of those to a new list. Having the root categories,then is possible to access all the 
  progeny (list of children that is attribute to Category) and with that we can recursively access the full tree of categories. 

# 6. Observations

* The data structure that we used in this US to store the tree, i.e List<Category>, could be substituted by another structure.
  A data structure like a binaryTree, but with more branches per Node, is a possible alternative.
  All the methods that support the US must then be converted to a recursive one.
  A class CategoryTree must also be created and stored where the actual List is stored.
  
* The implementation of a CategoryDTO class will be added during the next Sprint. This will enable a more safe and clean communication between UI and the business logic.




