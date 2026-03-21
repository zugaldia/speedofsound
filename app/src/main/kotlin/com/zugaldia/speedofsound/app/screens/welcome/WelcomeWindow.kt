package com.zugaldia.speedofsound.app.screens.welcome

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import com.zugaldia.speedofsound.app.STYLE_CLASS_BODY
import com.zugaldia.speedofsound.app.STYLE_CLASS_SUGGESTED_ACTION
import com.zugaldia.speedofsound.app.STYLE_CLASS_TITLE_1
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.Carousel
import org.gnome.adw.CarouselIndicatorDots
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gdk.Texture
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Image
import org.gnome.gtk.Justification
import org.gnome.gtk.Label
import org.gnome.gtk.LinkButton
import org.gnome.gtk.Orientation
import org.slf4j.LoggerFactory

private const val DEFAULT_WELCOME_WINDOW_WIDTH = 500
private const val DEFAULT_WELCOME_WINDOW_HEIGHT = 400
private const val LOGO_PIXEL_SIZE = 128
private const val DESCRIPTION_MAX_WIDTH_CHARS = 50

private const val LABEL_PREVIOUS = "Previous"
private const val LABEL_NEXT = "Next"
private const val LABEL_GET_STARTED = "Continue Setup"

class WelcomeWindow(
    app: Application,
    private val onGetStarted: () -> Unit
) : ApplicationWindow() {

    private val logger = LoggerFactory.getLogger(WelcomeWindow::class.java)
    private val pages = mutableListOf<Box>()
    private var currentIndex = 0

    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    init {
        application = app
        title = APPLICATION_NAME
        setDefaultSize(DEFAULT_WELCOME_WINDOW_WIDTH, DEFAULT_WELCOME_WINDOW_HEIGHT)
        resizable = false

        val carousel = buildCarousel()

        val dots = CarouselIndicatorDots()
        dots.carousel = carousel

        val buttonBox = buildButtonBox(carousel)

        val mainBox = Box(Orientation.VERTICAL, 0)
        mainBox.vexpand = true
        mainBox.hexpand = true
        mainBox.append(carousel)
        mainBox.append(dots)
        mainBox.append(buttonBox)

        val toolbarView = ToolbarView()
        toolbarView.addTopBar(HeaderBar())
        toolbarView.content = mainBox

        content = toolbarView

        carousel.onPageChanged { index ->
            currentIndex = index
            updateButtons()
        }

        updateButtons()
    }

    private fun buildCarousel(): Carousel {
        val carousel = Carousel()
        carousel.vexpand = true
        carousel.hexpand = true
        welcomePages.forEach { pages.add(buildPage(it)) }
        pages.forEach { carousel.append(it) }
        return carousel
    }

    private fun buildPage(welcomePage: WelcomePage): Box {
        val titleLabel = Label(welcomePage.title)
        titleLabel.addCssClass(STYLE_CLASS_TITLE_1)

        val descriptionLabel = Label(welcomePage.description)
        descriptionLabel.addCssClass(STYLE_CLASS_BODY)
        descriptionLabel.wrap = true
        descriptionLabel.maxWidthChars = DESCRIPTION_MAX_WIDTH_CHARS
        descriptionLabel.justify = Justification.CENTER

        val page = Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING)
        page.vexpand = true
        page.hexpand = true
        page.valign = Align.CENTER
        page.halign = Align.CENTER
        welcomePage.iconResourcePath?.let { buildLogoImage(it) }?.let { page.append(it) }
        page.append(titleLabel)
        page.append(descriptionLabel)
        welcomePage.url?.let { page.append(LinkButton.withLabel(it, it)) }
        return page
    }

    private fun buildLogoImage(resourcePath: String): Image? {
        val bytes = javaClass.getResourceAsStream(resourcePath)?.readBytes()
        if (bytes == null) {
            logger.warn("Could not load logo resource: $resourcePath")
            return null
        }

        return runCatching { Texture.fromBytes(bytes) }
            .onFailure { logger.warn("Could not decode logo resource: $resourcePath", it) }
            .getOrNull()
            ?.let { texture ->
                Image.fromPaintable(texture).apply { pixelSize = LOGO_PIXEL_SIZE }
            }
    }

    private fun buildButtonBox(carousel: Carousel): Box {
        prevButton = Button.withLabel(LABEL_PREVIOUS)
        prevButton.marginStart = DEFAULT_MARGIN
        prevButton.marginBottom = DEFAULT_MARGIN
        prevButton.onClicked { carousel.scrollTo(pages[currentIndex - 1], true) }

        nextButton = Button.withLabel(LABEL_NEXT)
        nextButton.addCssClass(STYLE_CLASS_SUGGESTED_ACTION)
        nextButton.marginEnd = DEFAULT_MARGIN
        nextButton.marginBottom = DEFAULT_MARGIN
        nextButton.onClicked { onNextClicked(carousel) }

        val spacer = Box(Orientation.HORIZONTAL, 0)
        spacer.hexpand = true

        val buttonBox = Box(Orientation.HORIZONTAL, 0)
        buttonBox.append(prevButton)
        buttonBox.append(spacer)
        buttonBox.append(nextButton)
        return buttonBox
    }

    private fun onNextClicked(carousel: Carousel) {
        if (currentIndex == pages.size - 1) {
            close()
            onGetStarted()
        } else {
            carousel.scrollTo(pages[currentIndex + 1], true)
        }
    }

    private fun updateButtons() {
        prevButton.visible = currentIndex > 0
        nextButton.label = if (currentIndex == pages.size - 1) LABEL_GET_STARTED else LABEL_NEXT
    }
}
