package project.diploma.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.diploma.domain.entities.Offer;
import project.diploma.domain.entities.Product;
import project.diploma.domain.models.service.OfferServiceModel;
import project.diploma.domain.models.service.ProductServiceModel;
import project.diploma.repository.OfferRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, ProductService productService, ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OfferServiceModel> findAllOffers() {
        return this.offerRepository.findAll()
                .stream()
                .map(o -> this.modelMapper.map(o, OfferServiceModel.class))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 300000)
    private void generateOffers() {

        this.offerRepository.deleteAll();
        List<ProductServiceModel> products = this.productService.findAllProducts();

        if (products.isEmpty()) {
            return;
        }

        Random random = new Random();
        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Offer offer = new Offer();
            offer.setProduct(this.modelMapper.map(products.get(random.nextInt(products.size())), Product.class));
            offer.setPrice(offer.getProduct().getPrice().multiply(new BigDecimal("0.8")));  //REMOVE " " if boom boom

            if (offers.stream().filter(o -> o.getProduct().getId().equals(offer.getProduct().getId())).count() == 0) {
                offers.add(offer);
            }
        }

        this.offerRepository.saveAll(offers);
    }
}