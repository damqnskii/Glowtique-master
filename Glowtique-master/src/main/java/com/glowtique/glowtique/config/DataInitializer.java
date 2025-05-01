package com.glowtique.glowtique.config;

import com.glowtique.glowtique.brand.repository.BrandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.rmi.server.LogStream.log;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        int brandCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM glowtiquedb.brand", Integer.class);
        int categoryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM glowtiquedb.category", Integer.class);
        int productsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM glowtiquedb.product", Integer.class);

        if (brandCount == 0) {
            jdbcTemplate.execute("""
                INSERT INTO glowtiquedb.brand (id, description, logo, name) VALUES
                    (0x0221173B83BE4158AA4D7A40C4693349, 'Dior made these lucky charms into house symbols at Dior and incorporated them into his designs. The charms included a four-leaf clover, a star, a bee, the number 8, and his favorite flower – the lily of the valley. Dior made the lily of the valley an essential part of his couture.', 'https://kreafolk.com/cdn/shop/articles/dior-logo-design-history-and-evolution-kreafolk_637ca925-15a9-4a0f-8b46-ee73ee0236eb.jpg?v=1717725054&width=2048', 'Christian Dior'),
                    (0x1CE810E3B3954732B316F3195FB1A0D1, 'Al Haramain Perfumes is based in United Arab Emirates. It produces traditional Arabian fragrances in several forms and concentrations: attars, home fragrances, incenses. Designer Al Haramain Perfumes has 331 perfumes in our fragrance base. Al Haramain Perfumes is a new fragrance brand.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRr3SL-XhutgsX7r3Kb0Lq8_EWKPjkn9sSIag&s', 'Al Haramain'),
                    (0x38CF031C8EA84BFBB36AC7C9DF9EDC65, 'Inspired by the life of its visionary creator, Loris Azzaro, they convey the very essence of hedonism, sensuality, and generosity. Azzaro fragrances evoke a profound reconnection to those sun-kissed moments in life.', 'https://1000logos.net/wp-content/uploads/2021/05/Azzaro-logo.png', 'Azzaro'),
                    (0x49D0CF5012E848199F125A29A40A7538, 'Versace, founded in 1978 by Gianni Versace, is an Italian luxury fashion company celebrated for its bold, glamorous designs and vibrant prints. Versace produces Italian-made ready-to-wear, accessories, and haute couture under its Atelier Versace brand.', 'https://wallpapers.com/images/hd/versace-logo-1920-x-1080-gnkh3mt0fpdfya1e.jpg', 'Versace'),
                    (0x4B78D577AF04408188D562ACA4D5003A, 'Burberry represents Modern British Luxury to the world. We do this by designing and manufacturing beautiful products, engaging our customers, challenging ourselves to be creative, and continuing to be a sustainable and responsible business.', 'https://wallpapers.com/images/hd/burberry-background-s7dpz3nr3byt8fn8.jpg', 'Burberry'),
                    (0x4CE44034D2C547839969194CCE750462, 'Chanel, established in 1910 by Coco Chanel in Paris, is a French luxury fashion house known for its timeless elegance and classic designs. The brand offers a wide range of products, including haute couture, ready-to-wear, accessories, fragrances, and beauty products.', 'https://www.hdwallpapers.in/download/chanel_in_black_background_with_butterflies_art_hd_chanel-1920x1080.jpg', 'Chanel'),
                    (0x5926ACB64D714DBFBD620DB27E95AE80, 'Abercrombie & Fitch is praised for its high-quality, long-lasting clothing including pants, cardigans, jeans, and jackets. Its particularly loved for its fitting styles, especially the curve love cut jeans for people with thicker thighs and narrow waists.', 'https://fontmeme.com/images/Abercrombie-Fitch-Logo.jpg', 'Abercrombie & Fitch'),
                    (0x78C4331A7224439D8E195C7A453037AE, 'Antonio Banderas Fragrances celebrates and reveals an adventurous vision of seduction. The luxury perfume brand was introduced in the late 1990s and reflects the irresistible charisma and elegance of the multi-faceted artist Antonio Banderas.', 'https://media.gettyimages.com/id/106746465/photo/madrid-spain-spanish-actor-antonio-banderas-presents-his-new-fragrance-the-secret-at-the.jpg?s=612x612&w=0&k=20&c=9rvTyFTMoZc2u0ql9EAeijmAKOA5mfUSct8VN6ni1W4=', 'Antonio Banderas'),
                    (0x79B9AEACBDFF4449ABA795DBD2D334F3, 'The use of large, round brilliant cut diamonds was a characteristic that made Bvlgari stand apart. Paired with baguette-cut diamonds, they gave extra sparkle to the creation with a sumptuousness that will be fully explored in the decades to come.', 'https://cdn.prod.website-files.com/6226155f1d139d1b4360f84d/6344a4bef1b2fab6c420fa08_10_SERPENTI_RG45_25fps_190219_generic_NEW.jpg', 'Bvlgari'),
                    (0x81F31411694F465AAC469D739B446F58, 'Scents from Hugo Boss vary from fresh apple tones to the elegance of cedarwood, portraying a symphony of innovation and style. A prominent offering for men includes Hugo Boss Bottled.', 'https://wallpapercave.com/wp/wp1936456.jpg', 'Hugo Boss'),
                    (0x9788F9F7CFA346E0858B25998D95728F, 'Discovered in 1916 by Baron Carlo Magnani, Acqua di Parma is renowned worldwide for its expert nose and unique olfactory offerings. Located in the heart of Parma, the brand emulates the la dolce vita lifestyle with its selection of signature scents.', 'https://hilandbeauty.com/images/uploaded/Acqua-Di-Parma-Logo-Copy-3.jpg', 'Acqua di Parma'),
                    (0x9834BB475C544FF7B34C784BCFF03671, 'The company specializes in haute couture, ready-to-wear, leather accessories, and footwear. Its cosmetics line, YSL Beauty, is owned by LOréal. Cédric Charbit has been CEO of Yves Saint Laurent since 2024, and Anthony Vaccarello creative director since 2016.', 'https://static.vecteezy.com/system/resources/previews/024/131/430/original/ysl-yves-saint-laurent-brand-logo-black-symbol-clothes-design-icon-abstract-illustration-with-brown-background-free-vector.jpg', 'Yves Saint Laurent'),
                    (0xA4E4A7D7B7EB495C8D9627BAA7720061, 'The brand is revered for its heritage and craftsmanship, with iconic scents like Aventus and Green Irish Tweed being popular among fragrance enthusiasts. Creeds perfumes are often associated with exclusivity, elegance, and long-lasting, unique aromas.', 'https://www.perfumeprice.co.uk/media/iopt/magezon/resized/1108x554/wysiwyg/Fragrances-like-Creed-Aventus-1600-x-800_1.webp', 'Creed'),
                    (0xA63AAC7482B141A6B759BDC69EC04D84, 'Xerjoff is an Italian luxury brand created by Sergio Momo in 2003, offering distinctive fragrances composed of the finest ingredients, in exquisite packaging. Designer Xerjoff has 183 perfumes in our fragrance base.', 'https://www.xerjoff.com/img/cms/Pagine%20CMS/Sponsorship/Moon%20Experience.png', 'Xerjoff'),
                    (0xE3B082F6C2B14D519373922769410269, 'Gucci, founded in 1921 in Florence, Italy, is a renowned luxury fashion house celebrated for its Italian craftsmanship, innovation, and timeless designs. The brand offers a wide range of products, including handbags, ready-to-wear, footwear, accessories, and home decoration.', 'https://wallpapersok.com/images/high/gucci-logo-wallpaper-hb9ghd1d5vdr96tc.jpg', 'Gucci');
""");
            System.out.println("Successfully initialized Brand in Glowtique DB");
        } else {
            System.out.println("Not needed initialization of Brands in Glowtique DB");
        }
        if (categoryCount == 0) {
            jdbcTemplate.execute("INSERT INTO glowtiquedb.category (id, category_type, description, name)\n" +
                    "VALUES  (0xA875DA6BEBE342ECA14A176E81821B44, 'PERFUME', 'Fragrances', 'Perfumes');");
            System.out.println("Successfully initialized Categories in Glowtique DB");
        } else {
            System.out.println("Not needed initialization of Categories in Glowtique DB");
        }
        if (productsCount == 0) {
            Path sqlFile = new ClassPathResource("product.sql").getFile().toPath();
            String sql = Files.readString(sqlFile);

            jdbcTemplate.execute(sql);
            System.out.println("Successfully initialized Products in Glowtique DB");
        } else {
            System.out.println("Not needed initialization of Products in Glowtique DB");
        }
    }
}
