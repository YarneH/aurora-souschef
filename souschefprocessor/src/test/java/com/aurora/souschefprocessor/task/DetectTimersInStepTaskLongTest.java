package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.facade.Delegator;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DetectTimersInStepTaskLongTest {
    private static Position irrelevantPosition = new Position(0, 1);

    @BeforeClass
    public static void initialize(){
        DetectTimersInStepTask.initializeAnnotationPipeline();
    }

 private RecipeTimer getTimer(String label) {
        String[] amountAndUnit = label.split(" ");
        int multiplier = 1;
        switch (amountAndUnit[1]) {
            case "MINUTES":
                multiplier = 60;
                break;
            case "HOURS":
                multiplier = 3600;
                break;
            default:
                multiplier = 1;
                break;
        }

        String[] amounts = amountAndUnit[0].split("-");
        if (amounts.length == 2) {
            int up = Integer.parseInt(amounts[0]) * multiplier;
            int low = Integer.parseInt(amounts[1]) * multiplier;
            return new RecipeTimer(up, low, irrelevantPosition);
        } else {
            return new RecipeTimer(Integer.parseInt(amounts[0]) * multiplier, irrelevantPosition);
        }
    }

    public String[] initializeDataSetTags() {
        return ("NO_TIMER\n" +
                "20-30 MINUTES\n" +
                "NO_TIMER\n" +
                "2 MINUTES\t2 MINUTES\t3-5 MINUTES\n" +
                "5 MINUTES\t2 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "2 MINUTES\n" +
                "NO_TIMER\n" +
                "30 MINUTES\n" +
                "NO_TIMER\n" +
                "6 MINUTES\t2 MINUTES\t2-3 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "90 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20 MINUTES\n" +
                "NO_TIMER\n" +
                "1-2 MINUTES\n" +
                "NO_TIMER\n" +
                "30 SECONDS\n" +
                "NO_TIMER\n" +
                "12-15 MINUTES\n" +
                "NO_TIMER\n" +
                "1 MINUTES\n" +
                "NO_TIMER\n" +
                "12 MINUTES\n" +
                "1-2 MINUTES\n" +
                "NO_TIMER\n" +
                "8-10 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "10 MINUTES\n" +
                "2 MINUTES\n" +
                "NO_TIMER\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "2 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15-20 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "4 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15 MINUTES\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "15 MINUTES\n" +
                "3 MINUTES\n" +
                "14 MINUTES\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "20 MINUTES\n" +
                "5 MINUTES\n" +
                "NO_TIMER\n" +
                "6-10 MINUTES\n" +
                "4 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "12 MINUTES\n" +
                "NO_TIMER\n" +
                "2 HOURS\n" +
                "2 MINUTES\n" +
                "5-7 MINUTES\n" +
                "2 MINUTES\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "5-7 MINUTES\t5 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "8 HOURS\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "45 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "18 MINUTES\n" +
                "NO_TIMER\n" +
                "35 MINUTES\n" +
                "90-120 SECONDS\n" +
                "15 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20-30 MINUTES\n" +
                "NO_TIMER\n" +
                "3 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20-30 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "2 MINUTES\n" +
                "30 MINUTES\n" +
                "NO_TIMER\n" +
                "30 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "30 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20 MINUTES\t25 MINUTES\t10 MINUTES\n" +
                "NO_TIMER\n" +
                "10 MINUTES\t5 MINUTES\t10 MINUTES\t15 MINUTES\n" +
                "NO_TIMER\n" +
                "2 MINUTES\n" +
                "2-3 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "7 MINUTES\n" +
                "5 MINUTES\t5 MINUTES\t2 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15-20 MINUTES\n" +
                "NO_TIMER\n" +
                "15 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "35-40 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "25-30 MINUTES\n" +
                "NO_TIMER\n" +
                "35-40 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "45-50 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "25 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20 MINUTES\t90 MINUTES\t30 MINUTES\n" +
                "NO_TIMER\n" +
                "5 MINUTES\t2-3 MINUTES\t5-7 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "5 HOURS\n" +
                "NO_TIMER\n" +
                "6-7 HOURS\t4-5 HOURS\n" +
                "7-10 MINUTES\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "30 MINUTES\n" +
                "1 HOURS\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "30 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "22-25 MINUTES\t2 HOURS\n" +
                "NO_TIMER\n" +
                "5 MINUTES\n" +
                "NO_TIMER\n" +
                "10-15 MINUTES\n" +
                "NO_TIMER\n" +
                "15-20 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "8 HOURS\n" +
                "NO_TIMER\n" +
                "6 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "5-6 MINUTES\t5 MINUTES\n" +
                "5 MINUTES\n" +
                "120 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15-30 MINUTES\t20 MINUTES\n" +
                "NO_TIMER\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "25-30 MINUTES\n" +
                "NO_TIMER\n" +
                "2 MINUTES\n" +
                "2 MINUTES\n" +
                "NO_TIMER\n" +
                "70 MINUTES\t10 MINUTES\n" +
                "8 HOURS\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "10 MINUTES\n" +
                "NO_TIMER\n" +
                "25-30 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "15 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "45-60 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "20 MINUTES\t10 MINUTES\n" +
                "NO_TIMER\n" +
                "NO_TIMER\n" +
                "40-45 MINUTES\t10-15 MINUTES\n" +
                "NO_TIMER\n" +
                "3 MINUTES\n" +
                "NO_TIMER\n" +
                "1 MINUTES\n" +
                "5-7 MINUTES\n" +
                "5 MINUTES\t5 MINUTES\n" +
                "NO_TIMER").split("\n");
    }

    public String[] initializeDataSet() {
        return ("Spoon coconut oil onto a baking sheet; add sweet potatoes, onion, apple, and garlic. Sprinkle cinnamon, chipotle pepper, and salt over sweet potato mixture.\n" +
                "Roast in the preheated oven, stirring occasionally, until sweet potatoes are tender, 20 to 30 minutes.\n" +
                "Process cauliflower in batches by pulsing in a food processor until it has the consistency of rice.\n" +
                "Heat olive oil in a large skillet over medium-high heat. Saute red bell pepper in hot oil until hot, about 2 minutes; add shallots and garlic and saute until soft, about 2 minutes. Stir cauliflower with the vegetables in the skillet; cook and stir until the cauliflower is hot, 3 to 5 minutes.\n" +
                "Pour chicken broth over the cauliflower mixture; season with salt and thyme. Bring the mixture to a simmer and cook until the liquid is absorbed, about 5 minutes. Stir lemon zest and lemon juice into the mixture; continue cooking until the lemon flavor penetrates the cauliflower, about 2 minutes more.\n" +
                "Remove skillet from heat, add olives and parsley, and toss to mix.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Cover baking sheet with parchment paper.\n" +
                "Blend almond four, egg, coconut oil, and flax seeds together in a blender. Transfer mixture to prepared baking sheet and roll or press out to desired thickness and shape.\n" +
                "Bake in the preheated oven until golden brown, about 20 minutes.\n" +
                "Stir coconut-almond milk blend, cocoa powder, maple syrup, brown sugar, vanilla extract, and salt together in a saucepan over medium heat; stir with a whisk to dissolve solids while the mixture heats to your desired temperature.\n" +
                "Blend 1/2 cup walnuts, dates, cinnamon, and cardamom together in a blender until almost smooth. Roll mixture into little balls. Place finely chopped walnuts in a shallow bowl and roll balls in walnuts to coat. Store balls in refrigerator.\n" +
                "Stir ketchup, water, corn syrup, molasses, Worcestershire sauce, hot sauce, steak sauce, garlic powder, onion powder, salt, black pepper, and nutmeg together in a saucepan over medium heat until smooth; bring to a boil and immediately remove from heat to cool completely.\n" +
                "Put 8 fluid ounces water, cashews, agave nectar, and vanilla extract in a blender; blend for 2 minutes. Strain mixture through cheesecloth or a fine-mesh sieve into a bowl.\n" +
                "Blend the strained bits of cashew mixture in the blender with remaining 8 fluid ounces water until smooth. Strain mixture through cheesecloth or sieve again.\n" +
                "Pour strained liquid into a bottle and add cinnamon; refrigerate until chilled, at least 30 minutes. Shake well before drinking.\n" +
                "Blend ice, milk, vanilla syrup, and green tea powder together in a blender until smooth.\n" +
                "Combine coconut water, rice, chicken bouillon, curry powder, cinnamon, chili powder, turmeric, and black pepper in a pot; bring to a boil. Reduce heat to low and simmer for 6 minutes. Stir cashews into rice mixture and cook for 2 minutes more. Let stand for 2 to 3 minutes; fluff rice with a fork.\n" +
                "Scoop avocado flesh into a bowl; mash with a potato masher and stir until somewhat smooth. Top avocado with onion and salsa. Sprinkle nutritional yeast and salt over the guacamole.\n" +
                "Blend navy beans, tahini, onion, miso paste, and nutritional yeast in a food processor until smooth. Add almond milk, lemon juice, salt, and cayenne pepper; process until creamy.\n" +
                "Preheat oven to 325 degrees F (165 degrees C).\n" +
                "Mix vital wheat gluten flour, nutritional yeast, mustard powder, paprika, fennel seeds, salt, black pepper, garlic powder, cayenne pepper, anise seeds, and sugar in a large mixing bowl.\n" +
                "Stir water, tomato paste, olive oil, liquid amino acid, and liquid smoke flavoring together in a separate bowl or measuring cup. Stir wet mixture into flour mixture until dough is evenly mixed. Turn dough onto a work surface and knead until smooth; shape into a log that is 1 1/2 to 2 inches in diameter. Tightly wrap log in aluminum foil, twisting the ends to secure.\n" +
                "Bake in the preheated oven for 90 minutes. Unwrap pepperoni and cool to room temperature; store in refrigerator in an air-tight container or wrapped in plastic wrap.\n" +
                "Mix flour, baking powder, and salt in a large bowl; add butter and crush into the flour mixture with hands until it resembles breadcrumbs.\n" +
                "Mix hot water and garlic into the flour mixture until it all comes together into a dough; turn out onto a cutting board dusted with flour. Knead dough 3 or 4 times. Cut dough into 10 pieces, roll into balls, and flatten into discs.\n" +
                "Cover dough with a clean kitchen towel and let sit for 20 minutes.\n" +
                "Heat a cast-iron skillet over-medium high heat.\n" +
                "Roll out one of the dough discs to about the size of your skillet. Cook in hot skillet until golden and puffy, 1 to 2 minutes per side. Remove to a platter and cover with a clean kitchen towel to keep warm. Repeat with remaining dough portions.\n" +
                "Heat olive oil in a cast iron skillet over medium heat. Add sausage; cook and stir, breaking up the sausage until no longer pink. Use a slotted spoon to transfer sausage to a dish.\n" +
                "Cook garlic in the skillet for 30 seconds; add mushrooms and saute until tender. Transfer to the dish with the sausage.\n" +
                "Pour water and Ragu(R) Classic Alfredo sauce into the skillet; whisk to blend. Bring to a boil.\n" +
                "Add broken lasagna noodles to the skillet; reduce heat to medium. Simmer until the noodles are tender, about 12 to 15 minutes, stirring regularly to prevent pasta from sticking to bottom of the pan.\n" +
                "Return sausage and mushrooms to the pan; reduce heat to low. Stir in spinach; cook until just wilted. Add ricotta and mozzarella cheeses, stirring until the mozzarella is melted and the ricotta is well combined.\n" +
                "Melt butter in skillet over medium-high heat. Add garlic, and cook for about 1 minute.\n" +
                "Stir in chicken and saute until the surface of the chicken loses most of its pink color. Add the broccoli; stir to coat. Stir in orzo.\n" +
                "Pour in the chicken stock and Ragu(R) Classic Alfredo Sauce. Stir well. Cover and reduce heat to medium. Cook until orzo is cooked, about 12 minutes.\n" +
                "Turn off heat and uncover. Stir and allow to rest for 1 to 2 minutes before serving.\n" +
                "Combine Ragu(R) Sauce and water in a large pot. Break the spaghetti in half (if desired) and place into the pot. Bring to a boil, stirring often.\n" +
                "Stir in artichoke hearts, kalamata olives, capers, garlic, grape tomatoes, crushed red pepper flakes, and parsley. Reduce heat to medium-low, cover, and simmer until pasta is cooked to desired tenderness, 8 to 10 minutes, stirring frequently.\n" +
                "Remove from the heat and serve warm. Garnish with additional fresh parsley and red pepper if desired.\n" +
                "Preheat oven to 425 degrees F (220 degrees C). Lightly oil a large baking dish or rimmed cookie sheet.\n" +
                "Trim chicken breasts of fat and slice in half length-wise to make 8 equal-thickness chicken cutlets. Note: If the chicken breasts are very large, pound them between plastic wrap into 1/2-inch thick cutlets.\n" +
                "Stir together flour, salt and black pepper in a shallow bowl. Whisk eggs in another bowl. In a third bowl, stir together Italian bread crumbs with parmesan cheese.\n" +
                "Dip both sides of each chicken cutlet into the flour, then dip in beaten eggs, letting excess egg drip off. Dip into the breadcrumb mixture, pressing cutlets into breading to ensure a thick, even coating of crumbs. Set prepared cutlets aside for 10 minutes.\n" +
                "Heat a large non-stick or cast iron pan over medium heat; add enough olive oil to coat the bottom of the pan. When oil is hot, add chicken in batches (don't crowd the pan), sauteing until golden brown, about 2 minutes per side.\n" +
                "Place chicken in prepared baking dish. Top each chicken breast with 2 tablespoons Ragu(R) Hearty Traditional Sauce. (Reserve remaining sauce to serve over pasta, if desired.) Then top with about 1/4 cup of shredded mozzarella.\n" +
                "Bake uncovered until cheese is melted and chicken is no longer pink in the center and the juices run clear, about 10 minutes. An instant-read thermometer inserted into the center should read at least 165 degrees F (74 degrees C).\n" +
                "Cook and drain the elbow noodles according to the package directions; set aside.\n" +
                "Brown the beef in a large skillet over medium heat. Drain excess grease. Stir in the Ragu(R) Sauce, chili powder, cumin, tomatoes and corn. Bring to a boil over medium high heat, cover,reduce to low and let simmer for 10 minutes.\n" +
                "Stir in the cooked noodles and 1 cup of the cheese until melted and heated through. Sprinkle the rest of the cheese evenly over top, heat on low until melted. Serve immediately.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Lightly oil a 3-quart baking dish.\n" +
                "Cook gnocchi per package directions, but remove them from water before they are completely cooked. Drain well and set aside.\n" +
                "Whisk egg with a pinch of salt and pepper in a shallow bowl. Mix together in a separate shallow bowl Italian bread crumbs, 1/4 cup grated Parmigiano-Reggiano cheese, salt and pepper.\n" +
                "Butterfly chicken breasts and bread them: dip each breast first into bread crumb mixture, then into egg, then into bread crumb mixture again.\n" +
                "Heat oil in a large skillet over medium heat. Cook breaded chicken until golden brown and cooked through (internal temperature at least 165 degrees F), about 5 minutes on each side. Remove chicken from skillet.\n" +
                "Toss and quickly saute gnocchi in the same skillet for about 2 minutes.\n" +
                "Spread a layer of Ragu(R) Sauce on the bottom of a 3-quart baking dish. Spread gnocchi evenly over the sauce. Sprinkle some mozzarella and Parmigiano cheese over gnocchi.\n" +
                "Layer sliced mushrooms over the top. Spread half of the remaining sauce over the mushrooms and sprinkle some more mozzarella and Parmigiano over the top. Salt and pepper to taste.\n" +
                "Slice chicken and place pieces on top of mushrooms and cheese. Spoon the remaining sauce evenly over the top. Sprinkle with the remaining cheeses. Season to taste with salt and pepper, if desired.\n" +
                "Bake until sauce is bubbly and cheese is melted, 15 to 20 minutes.\n" +
                "Cook broccoli florets and rotini according to package directions. Set aside.\n" +
                "Spray both sides of chicken with olive oil and evenly sprinkle with lemon pepper chicken.\n" +
                "Heat 1 tablespoon olive oil in large skillet over medium high heat. Cook chicken in skillet until cooked through, about 4 minutes per side (instant-read thermometer reads 165 degrees F). Remove chicken from skillet; cut into cubes and keep warm.\n" +
                "Wipe out skillet and return it to stove. Stir together Ragu(R) Classic Alfredo sauce, lemon zest, and lemon juice in skillet. Heat over medium-low heat until warmed through, stirring occasionally. Add pasta, broccoli, and chicken to skillet, and stir to combine. Cook until heated through.\n" +
                "Serve with grated Parmesan cheese, if desired.\n" +
                "Preheat oven to 400 degrees F (200 degrees C). Lightly grease a large rimmed baking sheet with olive oil.\n" +
                "Combine ground beef, bread crumbs, egg, mozzarella cheese, and garlic in a medium bowl. Use a 1/4 cup measure or ice cream scoop to portion into 12 to 13 meatballs. Place on prepared baking sheet.\n" +
                "Baked until browned and cooked through, about 15 minutes.\n" +
                "Bring the Ragu(R) Sauce to a simmer in a 12-inch skillet. Gently place the cooked meatballs into the sauce. Simmer 10 minutes.\n" +
                "Ladle some of the sauce over hot pasta and toss to coat. Serve with more sauce and the meatballs with a sprinkle of Parmesan cheese.\n" +
                "Coat chicken cubes in 1/2 cup Parmesan cheese and Italian seasoning.\n" +
                "Heat olive oil in a medium skillet over medium/high heat. Cook chicken in skillet, turning occasionally, until cooked through (no longer pink inside) about 5 minutes.\n" +
                "Pour water and Ragu(R) sauce into a saucepan and bring to a boil. Add pasta and simmer until pasta is tender, about 15 minutes. Stir frequently to prevent pasta from sticking to bottom. Add the remaining 1/2 cup Parmesan cheese and stir in chicken. Serve immediately. Enjoy!\n" +
                "Heat olive oil in a large Dutch oven or pot over medium-high heat. Add onion, garlic, and beef. Stirring frequently, cook until meat is browned and crumbly, about 3 minutes. Drain excess fat.\n" +
                "Stir in chicken broth, Ragu(R) sauce, diced tomatoes with mild green chilies, beans, chili powder, and cumin. Season with salt and pepper, to taste. Bring to a boil and stir in pasta; cover, reduce heat to a simmer and cook until pasta is al dente, about 14 minutes.\n" +
                "Remove from heat. Stir in the cheese and parsley. Serve immediately, garnished with additional cheese and parsley, if desired.\n" +
                "Heat oil in a large skillet over medium-high heat. Add mushrooms and cook until softened, about 5 minutes. Add red pepper flakes and spinach, cooking until spinach is wilted.\n" +
                "Stir in Ragu(R) Sauce and water. Bring to a boil. Stir in uncooked noodles. Reduce heat to medium and cook covered until noodles are tender, about 20 minutes. Note: be sure to stir frequently so the noodles do not settle at the bottom of the pan.\n" +
                "Top noodles with spoonfuls of ricotta cheese and then sprinkle with mozzarella cheese. Reduce heat to a simmer and cook covered until cheese is melted (about 5 minutes). Sprinkle with grated Parmesan cheese, if desired.\n" +
                "Preheat oven to 400 degrees F (200 degrees C). Lightly oil a rimmed baking sheet.\n" +
                "Combine sausage and bread crumbs in a bowl. Form into small balls. Place on a baking sheet. Bake until cooked through, 6 to 10 minutes.\n" +
                "Spread pizza crust onto baking sheet. Bake 4 minutes.\n" +
                "Mix together cooked spaghetti and 1 cup of the Ragu(R) sauce.\n" +
                "Evenly spread the pizza crust with 1/2 cup Ragu(R) sauce. Sprinkle with mozzarella cheese. Spread the spaghetti evenly over the cheese. Sprinkle with Parmesan cheese.\n" +
                "Bake 12 minutes. Serve immediately.\n" +
                "Stir mayonnaise, lime juice, Dijon mustard, sugar, and hot pepper sauce together in a bowl until smooth; season with salt and pepper.\n" +
                "Cover bowl with plastic wrap and refrigerate mustard sauce for at least 2 hours.\n" +
                "Mix beer, lemon juice, seafood seasoning, hot pepper sauce, garlic, cayenne pepper, and black pepper together in a saucepan; bring to a boil and add shrimp. Cook shrimp just until they are bright pink on the outside and the meat is no longer transparent in the center, about 2 minutes; drain immediately.\n" +
                "Heat a large skillet over medium-high heat. Cook and stir beef and black pepper in the hot skillet until browned and crumbly, 5 to 7 minutes; drain and discard grease.\n" +
                "Mix tomato sauce, tomatoes, salt, basil, oregano, garlic powder, onion powder, thyme, and red pepper flakes into ground beef; cook and stir until sauce is warmed through, about 2 minutes. Stir tomato paste into sauce.\n" +
                "Mix zucchini \"noodles\" into sauce, pressing down to fully submerge them; simmer over medium-low heat until zucchini is tender, about 10 minutes.\n" +
                "Mix beer, onion, red pepper flakes, parsley, salt, butter, garlic, pickling spice, oregano, and bay leaves in a saucepan; add enough cold water to fill the saucepan. Bring the mixture to a boil.\n" +
                "Drop shrimp into the boiling liquid, let the mixture return to a boil, and cook until shrimp are bright pink and the meat is no longer transparent in the center, about 5 minutes; drain.\n" +
                "Heat a large skillet over medium-high heat. Cook and stir beef in the hot skillet until browned and crumbly, 5 to 7 minutes; drain and discard grease. Add water and taco seasoning mix; cook and stir until water is evaporated and beef is evenly coated in seasoning mix, about 5 minutes.\n" +
                "Mix romaine lettuce, kidney beans, tomatoes, avocado, carrots, green bell pepper, celery, and Cheddar cheese in a large bowl; top with ground beef.\n" +
                "Whisk lime juice, honey, vinegar, cilantro, mustard, garlic, salt, and pepper together in a bowl until dressing is well mixed; served alongside salad.\n" +
                "Generously butter a 9x13-inch baking dish.\n" +
                "Whisk eggs, half-and-half, milk, white sugar, vanilla extract, 1 teaspoon cinnamon, 1 teaspoon nutmeg, and salt together in a bowl. Dip each bread slice into egg mixture and arrange in overlapping rows in the prepared baking dish. Cover dish with plastic wrap and refrigerate, 8 hours to overnight.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Mix pecans, brown sugar, butter, corn syrup, 1 teaspoon cinnamon, and 1 teaspoon nutmeg together in a bowl; spread evenly over bread in the baking dish.\n" +
                "Bake in the preheated oven until cooked through and golden brown, about 45 minutes. Generously top French toast with confectioners' sugar.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Mix 1 cup rice flour, butter, confectioners' sugar, potato starch, and tapioca starch together in a bowl until crumbly and evenly combined; press into a 9x13-inch baking dish.\n" +
                "Bake in the preheated oven until crust is bubbling, about 18 minutes.\n" +
                "Mix rhubarb, white sugar, eggs, 1/3 cup rice flour, and salt together in a bowl; spoon over crust.\n" +
                "Bake in the oven until topping is bubbling, about 35 minutes.\n" +
                "Stir sugar, milk, butter, and cocoa powder together in a saucepan over medium heat; bring to a boil and cook, stirring constantly, to dissolve sugar completely, 90 seconds to 2 minutes. Remove saucepan immediately from heat.\n" +
                "Stir peanut butter and vanilla extract into the milk mixture until smooth; add oats and stir to coat. Spoon the mixture onto a sheet of baking parchment and let sit until the mixture holds together, about 15 minutes.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Spread peach slices into a baking dish in a single layer. Stir brown sugar substitute, pecans, flour, and vanilla extract together; spread over the peach slices.\n" +
                "Bake in preheated oven until the peaches are heated through, 20 to 30 minutes.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Heat a small skillet over medium heat. Toast almonds in hot skillet until fragrant and lightly browned, about 3 minutes.\n" +
                "Put almonds and brown sugar into a food processor bowl and process until almonds are chopped; add butter and pulse the mixture until completely blended.\n" +
                "Arrange peaches with the cut sides facing up into the baking dish. Spoon 1 tablespoon almond mixture into the center of each peach. Pour liqueur and water into the baking dish; cover with aluminum foil.\n" +
                "Bake in preheated oven until the peaches are tender, 20 to 30 minutes.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Prepare a baking dish with cooking spray.\n" +
                "Arrange peaches cut-side up in the prepared baking dish.\n" +
                "Put butter in a saucepan over medium-low heat, add almonds, and heat together until butter melts, about 2 minutes. Stir honey, brown sugar, and cinnamon into the melted butter; bring to a simmer and add brandy and salt. Stir to dissolve the salt, remove saucepan from heat, and add vanilla extract; pour the mixture over the peaches.\n" +
                "Bake peaches in preheated oven until the peaches are cooked through, about 30 minutes.\n" +
                "Preheat oven to 450 degrees F (230 degrees C). Line a rimmed baking sheet with aluminum foil.\n" +
                "Put eggplant cubes in a colander set over a bowl. Sprinkle salt liberally over the eggplant cubes; let sit to drain for 30 minutes.\n" +
                "Toss tomatoes, zucchini, onion, and garlic together in a bowl. Drizzle olive oil over the mixture and toss to coat; season with salt and pepper.\n" +
                "Rinse eggplant to remove salt and pat dry with paper towel; add to the tomato mixture and stir. Add olive oil as necessary to assure the eggplant cubes are coated. Spread the vegetable mixture onto the prepared baking sheet.\n" +
                "Roast in preheated oven until the vegetables are tender, about 30 minutes.\n" +
                "Preheat oven to 400 degrees F (200 degrees C).\n" +
                "Brush 1 tablespoon olive oil over the red potatoes. Pierce potatoes with a fork and arrange onto a baking sheet. Spread baby eggplant pieces, red bell pepper slices, green bell pepper pieces, red onion, and garlic onto a separate baking sheet; drizzle 1 tablespoon olive oil over the vegetables. Stir 1/2 cup olive oil, garlic salt, oregano, basil, and black pepper together in a bowl.\n" +
                "Roast potatoes in preheated oven for 20 minutes. Put baking sheet into the oven and continue roasting potatoes and the vegetables until the potatoes are tender, about 25 minutes more. Let everything cool for 10 minutes.\n" +
                "Cut potatoes into bite-size chunks and put into a bowl. Drizzle olive oil and seasoning mixture over the potatoes and stir to coat; add the roasted vegetables and stir.\n" +
                "Melt butter in a heavy pot over medium heat; cook and stir onions until translucent, about 10 minutes. Add chicken broth and potatoes; bring to a boil. Reduce heat to medium-low, cover pot, and simmer, about 5 minutes. Add carrots and simmer, about 10 minutes. Add zucchini and simmer until all vegetables are tender, about 15 minutes.\n" +
                "Combine soup, basil, salt, and pepper into a blender or food processor no more than half full. Cover and hold lid down; pulse a few times before leaving on to blend. Puree in batches until desired consistency is reached.\n" +
                "Season eggplant slices with salt and pepper; let stand for 2 minutes.\n" +
                "Heat 2 tablespoons olive oil in a skillet over medium-high heat; saute 1/2 of the eggplant until golden brown, 2 to 3 minutes per side. Repeat with remaining olive oil and eggplant.\n" +
                "Preheat a panini press according to manufacturer's instructions.\n" +
                "Layer eggplant, roasted red pepper, and mozzarella cheese, respectively, onto the bottom piece of each flat bread. Spread 1 tablespoon hummus on the inside of each top piece of flat bread and place over the mozzarella layer, creating a panini.\n" +
                "Grill each panini on the preheated panini press until cooked through and cheese is melted, about 7 minutes.\n" +
                "Melt butter in a large saucepan over medium heat; add sugar, milk, and cocoa powder. Bring mixture to a boil, about 5 minutes; stir in peanut butter until melted, about 5 minutes. Return mixture to a boil and turn heat to low; simmer, stirring constantly, for 2 minutes. Remove saucepan from heat; stir in vanilla extract.\n" +
                "Fold oats into mixture until thoroughly mixed. Drop mixture by spoonfuls onto a sheet of waxed paper; cool to room temperature.\n" +
                "Preheat oven to 400 degrees F (200 degrees C).\n" +
                "Arrange brown sugar, 1 teaspoon per peach, in a 9x13-inch baking dish. Top each brown sugar mound with a piece of butter and a sprinkle of cinnamon. Place a peach half, cut-side down, on top of brown sugar-butter.\n" +
                "Bake in the preheated oven until peaches are soft, 15 to 20 minutes.\n" +
                "Plate 2 warm peaches per serving and top with 1 scoop vanilla ice cream.\n" +
                "Place strawberries in a container with a lid; add sugar and stir to coat. Place lid on the container and refrigerate until sugar has dissolved, at least 15 minutes.\n" +
                "Place a 1 dessert cup in each serving bowl and smother with strawberries. Top each with whipped cream.\n" +
                "Preheat oven to 375 degrees F (190 degrees C).\n" +
                "Spread 1/2 of the yellow cake mix into a 9x13-inch baking dish.\n" +
                "Cut peaches from the larger can into large chunks and layer on top of the cake mix. Sprinkle butter over peach layer. Cover butter layer with remaining cake mix. Pour the reserved juice from both cans over cake mix mixture. Set the peaches from smaller can aside for another use.\n" +
                "Bake in the preheated oven until cobbler is lightly browned, 35 to 40 minutes.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Mix 2 cups flour, butter, and 1/2 cup white sugar together in a bowl until crumbly; press into the bottom of a 12x9-inch baking dish.\n" +
                "Bake in preheated oven until crust is light brown, 25 to 30 minutes.\n" +
                "Beat eggs, 1 cup sugar, and 6 tablespoons flour together in a bowl until smooth; add rhubarb, coconut, lemon juice, and vanilla extract and stir to coat. Pour the rhubarb mixture over the crust.\n" +
                "Bake in preheated oven until the fruit layer is set, 35 to 40 minutes. Let bars cool completely before serving.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Grease a 13x9-inch baking dish.\n" +
                "Mix 2 cups flour, 1/2 cup brown sugar, and 1/2 teaspoon cinnamon together in a bowl. Cut 1 cup unsalted butter into the flour mixture until crumbs form; press into bottom of prepared baking dish.\n" +
                "Bake crust in preheated oven until golden brown, about 15 minutes.\n" +
                "Blend eggs, white sugar, 1/2 cup flour, and salt in a blender until smooth; pour into a large bowl. Fold rhubarb, strawberries, and blueberries into the blended egg mixture. Spread the fruit mixture onto the baked crust.\n" +
                "Mix 1/2 cup flour, 1/2 cup brown sugar, oats, melted butter, and 1/2 teaspoon cinnamon together in a bowl; sprinkle over the fruit mixture.\n" +
                "Bake in preheated oven until lightly browned on top and the filling has set, 45 to 50 minutes.\n" +
                "Preheat oven to 400 degrees F (200 degrees C). Prepare 12 muffin cups with cooking spray.\n" +
                "Mix flour, baking powder, stevia, cinnamon, sea salt, and nutmeg together in a large bowl. Beat skim milk, egg, and margarine together in a separate bowl; add to flour mixture and stir just until the dry mixture is moistened. Gently fold minced apple through the batter. Spoon batter into the prepared muffin cups.\n" +
                "Bake in preheated oven until lightly browned on the tops, about 25 minutes.\n" +
                "Blend water, peach slices, cucumber, kale, lemon, and ginger in a blender until smooth.\n" +
                "Preheat oven to 200 degrees F (95 degrees C). Line 2 baking sheets with baking parchment.\n" +
                "Mix oats, wheat germ, oat bran, almonds, flaxseed, pumpkin seeds, and salt together in a bowl. Beat maple syrup and egg white together in a separate bowl; pour into the oat mixture and stir to coat completely. Spread the mixture onto prepared baking sheets.\n" +
                "Bake in preheated oven, rotating pans every 20 minutes, for 90 minutes. Increase heat to 250 degrees F (120 degrees C) and continue baking until golden brown, about 30 minutes more.\n" +
                "Transfer the oat mixture to a large bowl, add coconut and cranberries, and stir. Let granola cool to room temperature.\n" +
                "Heat oil in skillet over medium heat. Cook and stir onion in hot oil until translucent, about 5 minutes. Break ground turkey into small chunks and add to skillet; cook and stir until beginning to brown, 2 to 3 minutes. Sprinkle taco seasoning over the turkey mixture; continue to cook and stir until turkey is completely browned through, 5 to 7 minutes more.\n" +
                "Pile lettuce into the bottom of a large salad bowl; top with the turkey mixture, corn, black beans, avocado, green onions, Cheddar cheese, and spicy ranch dressing.\n" +
                "Season rump roast generously with garlic powder, salt, and pepper; put into the crock of a slow cooker. Pour water into the crock.\n" +
                "Cook on High for 5 hours. Remove roast to a cutting board and shred with 2 forks.\n" +
                "Stir tomatoes, tomato paste, ginger, curry powder, kosher salt, and black pepper together in a slow cooker crock until smooth; add cauliflower, chicken, onion, and raisins and stir.\n" +
                "Cook on Low until the chicken is cooked through, 6 to 7 hours (or on High for 4 to 5 hours). Garnish with cilantro.\n" +
                "Heat a large skillet over medium-high heat. Cook and stir beef, onion, red bell pepper, mushrooms, salt, and chili powder together in the hot skillet until the beef is completely browned, 7 to 10 minutes; drain and discard grease.\n" +
                "Spread spinach leaves into a salad bowl; top with beef mixture. Add black beans and chopped tomato.\n" +
                "Stir polenta and chicken broth together in a saucepan over medium heat; cook and stir until the polenta is heated through completely, about 5 minutes.\n" +
                "Remove pan from heat. Stir Parmesan cheese, black pepper, and paprika with the polenta mixture.\n" +
                "Arrange roast beef slices onto the flour tortilla; top with lettuce leaves, red bell pepper leaves, blue cheese, and blue cheese salad dressing. Roll tortilla around ingredients.\n" +
                "Stir brown sugar, soy sauce, white sugar, water, white wine vinegar, rice vinegar, and ground ginger together in a saucepan; bring to a boil, reduce heat to medium-low, and cook until reduced in volume by half, about 30 minutes.\n" +
                "Let glaze cool to thicken, at least 1 hour.\n" +
                "Toss green cabbage, red cabbage, carrots, and cilantro together in a large bowl.\n" +
                "Beat peanut oil and lime juice together with a whisk until smooth; add sugar and beat to dissolve. Drizzle dressing over the slaw and toss to coat; season with kosher salt and black pepper.\n" +
                "Cover bowl with plastic wrap and refrigerate at least 30 minutes.\n" +
                "Mix kosher salt, smoked paprika, onion powder, garlic powder, oregano, black pepper, brown sugar, and cumin together in a sealable container. Seal container and shake to mix.\n" +
                "Place ice cubes in a cocktail shaker; top with blood orange juice, vodka, and orange-flavored liqueur. Cover shaker and shake; strain drink into a martini glass. Top drink with orange juice and garnish with lime wedge.\n" +
                "Place a 10-inch tube pan in the oven. Preheat oven to 425 degrees F (220 degrees C).\n" +
                "Beat egg whites with salt in a glass or metal bowl until foamy. Add cream of tartar and continue to beat until egg whites stand in peaks. Gradually add honey while beating continually to integrate until stiff peaks form. Lift your beater or whisk straight up: the egg whites will form sharp peaks.\n" +
                "Sift flour and white sugar together. Add 1/3 of the flour mixture into the egg whites and fold gently with a spoon to integrate; repeat in two more additions of the flour mixture to completely incorporate. Stir vanilla into the batter.\n" +
                "Remove hot tube pan from the preheated oven. Pour the batter into the hot pan. Thump pan several times on a flat surface to break air bubbles in batter.\n" +
                "Bake in the preheated oven until golden and the top springs back when lightly pressed, 22 to 25 minutes. Cool cake in pan on a wire pastry rack for at least 2 hours. Run a long, thin metal spatula or knife around edge of pan to loosen cake from pan.\n" +
                "Mix honey, turmeric, coconut oil, and black pepper together in a resealable container using a butter knife until thoroughly combined. Cover and store in the refrigerator.\n" +
                "Place boiling water in a bowl and add green tea; allow to steep for 5 minutes.\n" +
                "Blend orange, pineapple, and kiwi fruit together in a blender. Remove tea bag from tea and add tea to orange mixture; blend. Add yogurt and blend until smooth.\n" +
                "Combine vodka, raspberries, sugar, and water in a large, non-metallic container. Press and muddle raspberries until incorporated into the mixture. Allow mixture to sit, stirring for 10 to 15 minutes each day, for 5 to 6 days.\n" +
                "Strain mixture multiple times through a fine-mesh strainer or cheesecloth until berries and seeds are removed and liquid is lighter in color.\n" +
                "Stir carrots, pears, water, sugar, and cinnamon together in a saucepan over medium heat. Cover the saucepan with a lid and cook until carrots are soft, 15 to 20 minutes. Cool and mash with a fork or potato masher to desired consistency.\n" +
                "Cut 10 strips of parchment paper, 2-inches wide by 16-inches long.\n" +
                "Sift confectioner's sugar and meringue powder together in a bowl; beat in water and vanilla extract until smooth.\n" +
                "Divide sugar mixture evenly into three bowls; beat in small amounts of different food coloring paste into each bowl until desired color has been achieved.\n" +
                "Place a #3 pastry tip into end of pastry bag; fill with one of the tinted sugar mixtures. Pipe buttons about the size of a penny onto parchment strips, 3 rows across. Repeat with remaining tinted sugar mixtures. Dry until buttons are hard, about 8 hours.\n" +
                "Heat oil in a deep-fryer or large saucepan to 350 degrees F (175 degrees C).\n" +
                "Fill a saucepan with 1/2-inch water; bring to a boil. Carefully place cold eggs into saucepan, cover the saucepan with a lid, reduce heat to medium-high, and cook until eggs yolks are soft, about 6 minutes. Remove from heat and pour cold water over eggs to halt the cooking process. Cool eggs to room temperature in cold water; peel and dry eggs on paper towels.\n" +
                "Mix sausage, mustard, nutmeg, and cayenne pepper together in a bowl. Shape into 6 equal-size balls.\n" +
                "Lay a piece of plastic wrap on a flat work surface. Place 1 ball of sausage mixture into the center of the plastic wrap, fold the plastic wrap over sausage, and flatten into an 1/8-inch thick oval shape. Pull plastic wrap back and place 1 egg in the center of the sausage. Pick up plastic wrap, moisten fingertips, and press sausage around egg to cover completely, sealing sausage around egg. Repeat with remaining eggs and sausage.\n" +
                "Place bread crumbs in a shallow bowl. Pour flour into another shallow bowl. Beat remaining 2 eggs in another shallow bowl.\n" +
                "Gently press eggs into flour to coat; shake off excess flour. Dip eggs into the beaten egg, then press into bread crumbs. Gently toss between your hands so any bread crumbs that haven't stuck can fall away. Place the breaded eggs onto a plate.\n" +
                "Working in batches, cook eggs in the preheated oil until golden, 5 to 6 minutes. Transfer to a wire rack to cool, at least 5 minutes.\n" +
                "Gently whisk olive oil and honey together in a small pot over low heat until smooth and warm, about 5 minutes. Remove pot from heat and cool.\n" +
                "Beat goat cheese and oil-honey mixture together in a bowl using an electric mixer until creamy; refrigerate at least 2 hours.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Grease 12 muffin cups or line with paper liners.\n" +
                "Whisk flour, sugar, coconut, cinnamon, baking soda, nutmeg, and salt together in a large bowl; add carrots, oil, pineapple, eggs, and vanilla extract and mix until batter is evenly combined. Fold chocolate chips into batter; spoon into the prepared muffin cups.\n" +
                "Bake in the preheated oven until a toothpick inserted in the center of a muffin comes out clean, 15 to 30 minutes. Cool muffins for 20 minutes before serving.\n" +
                "Preheat oven to 325 degrees F (165 degrees C). Line muffin cups with paper liners.\n" +
                "Whisk almond milk and vinegar together in a small bowl; set aside until curdled, about 10 minutes.\n" +
                "Mix whole wheat flour, all-purpose flour, baking powder, and baking soda together in a bowl. Combine carrots, pineapple, raisins, and walnuts together in a separate bowl.\n" +
                "Whisk 3/4 cup plus 2 tablespoons white sugar, olive oil, molasses, vanilla extract, cinnamon, nutmeg, salt, and cardamom together in a separate bowl. Mix curdled milk mixture and sugar mixture into flour mixture just until batter is mixed; fold in carrot mixture. Spoon batter into muffin cups until each is completely filled and domed.\n" +
                "Bake in the preheated oven until a toothpick inserted in the center comes out clean, 25 to 30 minutes.\n" +
                "Position the oven racks in the center and lower third of the oven. Preheat oven to 350 degrees F (175 degrees C). Fill a roasting pan halfway with water and place in the lower rack in the oven. Spray the inside of a 9-inch springform pan with cooking spray.\n" +
                "Beat three 8-ounce packages cream cheese in a bowl using an electric mixer on high speed until fluffy; reduce speed to medium-low. Add white sugar and beat 2 minutes. Beat 4 eggs, 1 at a time, into cream cheese mixture, beating well before adding the next egg. Beat sour cream and vanilla extract into cream cheese mixture until smooth; add 3 tablespoons flour and beat until cheesecake mixture is smooth.\n" +
                "Beat brown sugar and canola oil together in a separate bowl using an electric mixer on medium speed until smooth. Add 1 egg and beat for 2 minutes; reduce speed to low. Add 1 cup flour, cinnamon, baking powder, and salt and beat until batter is combined. Fold carrots and walnuts into batter.\n" +
                "Spread carrot cake batter in the prepared pan; top with cheesecake mixture. Place pan on the rack above the roasting pan with water in the oven.\n" +
                "Bake in the preheated oven until cheesecake is set, about 1 hour 10 minutes, covering top of pan with aluminum foil if browning too quickly. Cool for 10 minutes.\n" +
                "Run a knife around the edge of pan to loosen cake; cool to room temperature in pan on a wire rack. Refrigerate at least 8 hours. Remove the outside of the springform pan.\n" +
                "Beat confectioners' sugar, 4 ounces cream cheese, and almond extract together in a bowl using an electric mixer until frosting is smooth. Spread frosting on top of cheesecake layer.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Grease 6 jumbo muffin cups or line with paper liners.\n" +
                "Soak dried cranberries in a bowl of hot water until plump, about 10 minutes; drain.\n" +
                "Combine whole wheat flour, white sugar, oat flour, baking powder, baking soda, salt, cinnamon, and nutmeg together in a bowl. Beat sweet potatoes, eggs, and coconut oil together in a separate bowl until smooth; stir into flour mixture until batter is smooth. Fold plump sweetened cranberries and pecans into batter; spoon into prepared muffin cups.\n" +
                "Bake in the preheated oven until a toothpick inserted in the center of a muffin comes out clean, 25 to 30 minutes.\n" +
                "Preheat oven to 350 degrees F (175 degrees C).\n" +
                "Process Cheddar cheese, flour, butter, 1/4 cup Parmesan cheese, cayenne pepper, and salt in a food processor until large crumbs form; transfer to a bowl. Stir milk into the cheese mixture until it forms a ball of dough.\n" +
                "Roll dough out onto a lightly floured work surface to about 1/8-inch thick. Use a pizza cutter or thin knife to trim the edges of the dough to leave a large square with sides measuring 7 to 8 inches. Cut dough into 1/3-inch strips and arrange onto a baking sheet.\n" +
                "Bake in preheated oven until puffy, about 15 minutes. Sprinkle 2 tablespoons Parmesan cheese over the warm cheese straws.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Butter an 8-inch square baking dish.\n" +
                "Mix 1 cup flour, milk, melted butter, 1/2 cup brown sugar, vanilla extract, 1 teaspoon cinnamon, and baking powder together in a bowl until batter is combined.\n" +
                "Combine 1/4 cup brown sugar, 4 teaspoons flour, 1 1/2 teaspoons cinnamon, and allspice together in a bowl; add water and mix. Stir peaches into brown sugar filling. Spoon filling into the prepared baking dish. Spread batter over the filling; top with white sugar.\n" +
                "Bake in the preheated oven until filling is bubbling and top is golden brown, 45 to 60 minutes.\n" +
                "Place yogurt in a bowl. Sift matcha powder over yogurt and whisk until evenly blended.\n" +
                "Preheat oven to 350 degrees F (175 degrees C). Line 12 muffin cups with paper muffin liners.\n" +
                "Whisk flour, green tea powder, baking powder, and salt together in a bowl. Beat sugar and oil together in another bowl; whisk in eggs and vanilla extract. Pour sugar mixture into flour mixture and stir gently until batter is just-combined. Stir applesauce into batter. Spoon batter into prepared muffin cups.\n" +
                "Bake in the preheated oven until a toothpick inserted into the center of a cupcake comes out clean, about 20 minutes. Cool in the pan for 10 minutes before removing to a wire rack to cool completely.\n" +
                "Preheat oven to 450 degrees F (230 degrees C). Line a roasting pan or baking sheet with aluminum foil and coat with cooking spray.\n" +
                "Arrange tomatoes, cut-side down, in the roasting pan; add Anaheim chile peppers, jalapeno peppers, and poblano peppers, all skin-side up. Add onions and garlic to roasting pan. Spray the vegetable mixture with cooking spray.\n" +
                "Roast in the preheated oven until tomato and chile pepper skins are blistered and charred, 40 to 45 minutes. Remove from oven and cool for 10 to 15 minutes, keeping skins on tomatoes and chile peppers.\n" +
                "Blend tomato-chile pepper mixture, lime juice, cilantro, cider vinegar, oregano, cumin, kosher salt, black pepper, and celery salt in a food processor using quick pulses until desired consistency is reached. Refrigerate salsa in an air-tight container.\n" +
                "Beat cream cheese in a bowl with an electric mixer until smooth and creamy, about 3 minutes. Gradually beat confectioners' sugar into cream cheese. Beat in pudding mix; add milk until desired consistency is reached. Fold in whipped topping.\n" +
                "Preheat oven to 400 degrees F (200 degrees C). Grease the bottom of a 13x9-inch baking dish.\n" +
                "Bring a large pot of water to a boil. Cook zucchini halves in boiling water to just soften slightly, about 1 minute. Remove zucchini halves from the water and arrange into the prepared baking dish with the cut sides facing upwards.\n" +
                "Heat a large skillet over medium-high heat. Cook and stir ground turkey in the hot skillet until browned and crumbly, 5 to 7 minutes; drain and discard grease.\n" +
                "Return skillet to medium-high heat; pour water over the turkey and season with taco seasoning. Cook until the water thickens and coats the turkey, about 5 minutes. Add red peppers, diced tomatoes and green chiles, onion, and cilantro; cook and stir until the peppers are softened, about 5 minutes more.\n" +
                "Stir sour cream and sour cream seasoning mix together in a bowl.").split("\n");
    }

    @Test
    public void DetectTimersInStep_doTask_AccuracyOnDataSet() {
        /**
         * Timers should be detected correctly at least 98% of the time
         */
        String[] dataSet = initializeDataSet();
        String[] dataSetTags = initializeDataSetTags();
        int amount = dataSet.length;
        int correct = amount;
        for (int i = 1; i <= dataSet.length; i++) {

            String stepString = dataSet[i - 1];
            String tag = dataSetTags[i - 1];

            RecipeStep step = new RecipeStep(stepString);
            ArrayList<RecipeStep> list = new ArrayList<>();
            list.add(step);

            RecipeInProgress rip = new RecipeInProgress("irrelevant");
            rip.setRecipeSteps(list);

            DetectTimersInStepTask detector = new DetectTimersInStepTask(rip, 0);
            detector.doTask();

            List<RecipeTimer> timers = step.getRecipeTimers();

            if (tag.equals("NO_TIMER")) {
                // No timers but one detected
                if (timers.size() > 0) {
                    correct--;
                    System.out.println(i + " " + timers + " " + stepString);
                }
            } else {
                String[] labels = tag.split("\t");

                if (labels.length != timers.size()) {
                    // not the correct amount of timers
                    correct--;
                    System.out.println(i + " realSize: " + labels.length + " " + timers + " " + stepString);
                } else {
                    for (int j = 0; j < labels.length; j++) {
                        String label = labels[j];
                        RecipeTimer tim = getTimer(label);

                        if (!tim.equals(timers.get(j))) {
                            correct--;
                            System.out.println(i + " " + tim + " " + timers.get(j) + " " + stepString);
                        }
                    }
                }
            }


        }

        assert (correct * 100.0 / amount > 98);
        System.out.println(correct + " correct out of " + amount + " tested. Accuracy: " + correct * 100.0 / amount);
    }

}
